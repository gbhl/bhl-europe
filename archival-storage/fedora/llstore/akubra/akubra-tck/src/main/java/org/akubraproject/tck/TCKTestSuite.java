/* $HeadURL$
 * $Id$
 *
 * Copyright (c) 2009-2010 DuraSpace
 * http://duraspace.org
 *
 * In collaboration with Topaz Inc.
 * http://www.topazproject.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.akubraproject.tck;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.transaction.Transaction;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.MissingBlobException;

/**
 * A suite of unit tests for Akubra stores.
 *
 * <p>The tests are assigned, and depend on, various groups. All tests here depend on (directly or
 * indirectly) the group "init", and there is one dummy test in that group; all tests that
 * manipulate blobs (create, delete, modify, move) are in the "manipulatesBlobs" group; there is
 * one test in the "post" group, and that test depends on the "manipulatesBlobs" to ensure it is
 * run after all blob manipulations are completed. Additional there are three groups that represent
 * the entity they're testing: "store", "connection", and "blob".
 *
 * <p>Subclasses of this must implement at least {@link #getInvalidId getInvalidId}. Additionally,
 * many stores will probably want to override {@link #createId createId} and {@link #getPrefixFor
 * getPrefixFor} in order to create URI's which are understood by the store.
 *
 * <p>For stores that don't support all modify operations, various flags can be passed to the
 * constructor indicating what is supported. This works fine for the transactional, id-gen, and
 * move-to flags; however, create, delete, and list-ids are needed throughout the tests. For
 * stores that don't support one or more of these latter flags you must also override one or more
 * of {@link #createBlob(BlobStoreConnection, Blob, String) createBlob} and {@link
 * #deleteBlob(BlobStoreConnection, Blob) deleteBlob} to create and delete the underlying
 * resources, and {@link #assertNoBlobs(String) assertNoBlobs} and {@link
 * #listBlobs(BlobStoreConnection, String, URI[]) listBlobs} for verifying things got cleaned up
 * properly.
 *
 * @author Ronald Tschalär
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public abstract class TCKTestSuite extends AbstractTests {
  protected final URI     storeId;
  protected final boolean isIdGenSupp;
  protected final boolean isListIdsSupp;
  protected final boolean isOutputSupp;
  protected final boolean isDeleteSupp;
  protected final boolean isMoveToSupp;
  protected final boolean isSyncSupp;

  /**
   * Create a new test suite instance. This assumes a fully functional store, i.e. one that
   * supports creation, deletion, moving, and listing of blobs.
   *
   * @param store           the store to test
   * @param storeId         the store's expected id
   * @param isTransactional if the store is transactional
   * @param isIdGenSupp     true if id generation (<code>getBlob(null, ...)</code>) is supported
   */
  protected TCKTestSuite(BlobStore store, URI storeId, boolean isTransactional,
                         boolean isIdGenSupp) {
    this(store, storeId, isTransactional, isIdGenSupp, true, true, true, true, true);
  }

  /**
   * Create a new test suite instance.
   *
   * @param store           the store to test
   * @param storeId         the store's expected id
   * @param isTransactional if the store is transactional
   * @param isIdGenSupp     true if id generation (<code>getBlob(null, ...)</code>) is supported
   * @param isListIdsSupp   true if <code>con.listBlobIds()</code> is supported
   * @param isOutputSupp    true if <var>Blob.openOutputStream()</var> is supported
   * @param isDeleteSupp    true if <var>Blob.delete()</var> is supported
   * @param isMoveToSupp    true if <var>Blob.moveTo()</var> is supported
   * @param isSyncSupp      true if <var>con.sync()</var> is supported
   */
  protected TCKTestSuite(BlobStore store, URI storeId, boolean isTransactional, boolean isIdGenSupp,
                         boolean isListIdsSupp, boolean isOutputSupp, boolean isDeleteSupp,
                         boolean isMoveToSupp, boolean isSyncSupp) {
    super(store, isTransactional);
    this.storeId       = storeId;
    this.isIdGenSupp   = isIdGenSupp;
    this.isListIdsSupp = isListIdsSupp;
    this.isOutputSupp  = isOutputSupp;
    this.isDeleteSupp  = isDeleteSupp;
    this.isMoveToSupp  = isMoveToSupp;
    this.isSyncSupp    = isSyncSupp;
  }

  /**
   * Create an id for the given name. Usually this will incorporate the given name as part of the
   * URI itself (e.g. by appending it to a fixed prefix), but for stores that can't do this (e.g.
   * a read-only store) they can return an arbitrary URI. This must return different URI's for
   * different names and the same URI for the same name, at least for the duration of each test.
   *
   * <p>The default implementation returns <code>URI.create("urn:akubra-tck:" + name)</code>.
   *
   * @param name  the URI's "name"
   * @return the URI
   * @see #getPrefixFor
   */
  protected URI createId(String name) {
    return URI.create("urn:akubra-tck:" + name);
  }

  /**
   * Get the URI prefix for the given name. This is used by the tests to list all blobs created by
   * the test in order to verify things got cleaned up. The <var>name</var> passed in here will be
   * a prefix of the names passed to previous {@link #createId} calls. The expectation is that if
   * <code>createId</code> is overriden then so must this method. In any case the implementation of
   * this method must figure out an appropriate prefix to return that will include all created id's
   * (in the current test).
   *
   * <p>The default implementation returns <code>"urn:akubra-tck:" + name</code>.
   *
   * @param name  the URI's "name"
   * @return the prefix
   */
  protected String getPrefixFor(String name) {
    return "urn:akubra-tck:" + name;
  }

  /**
   * @return an invalid id (for id validation test), or null to skip the tests (e.g. if all id's
   *         are valid)
   */
  protected abstract URI getInvalidId();

  /**
   * @param uri the uri for which to get a list of aliases
   * @return an array of aliases, or null if getCanonicalId() returns null
   */
  protected abstract URI[] getAliases(URI uri);

  @Test(groups={ "init" })
  public void testInit() {
  }

  /*
   * BlobStore Tests
   */

  /**
   * Store id should be what it was initialized with.
   */
  @Test(groups={ "store" })
  public void testGetId() {
    assertEquals(store.getId(), storeId);
  }

  /**
   * Request to open a connection without a transaction.
   */
  @Test(groups={ "store" }, dependsOnGroups={ "init" })
  public void testOpenConnectionNoTransaction() {
    try {
      BlobStoreConnection con = store.openConnection(null, null);
      if (isTransactional)
        fail("Did not get expected IOException initializing store without a transaction");

      assertNotNull(con, "Null connection returned from openConnection(null)");
      assertSame(con.getBlobStore(), store);
    } catch (IOException ioe) {
      if (!isTransactional)
        fail("Got unexpected IOException initializing store without a transaction", ioe);
    }
  }

  /**
   * Request to open a connection with a transaction.
   */
  @Test(groups={ "store" }, dependsOnGroups={ "init" })
  public void testOpenConnectionWithTransaction() throws Exception {
    tm.begin();
    BlobStoreConnection con = null;

    try {
      con = store.openConnection(tm.getTransaction(), null);
      if (!isTransactional)
        fail("Did not get expected UnsupportedOperationException while initializing store with " +
             "a transaction");

      assertNotNull(con, "Null connection returned from openConnection(txn)");
      assertSame(con.getBlobStore(), store);
    } catch (UnsupportedOperationException uoe) {
      if (isTransactional)
        fail("Got unexpected UnsupportedOperationException initializing store with a transaction",
             uoe);
    } finally {
      tm.rollback();
      if (con != null)
        con.close();
    }
  }

  /*
   * BlobStoreConnection Tests
   */

  /**
   * Test closing a connection.
   */
  @Test(groups={ "connection", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testCloseConnection() throws Exception {
    final URI id = createId("blobCloseConn1");

    // no operations, close then commit
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
        }
    }, true);

    // no operations, close then roll back
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
        }
    }, false);

    // one operation, close then commit
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, null);
          createBlob(con, b, null);
        }
    }, true);

    // one operation, close then roll back
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, "");
          deleteBlob(con, b);
        }
    }, false);

    // clean up
    if (isTransactional)
      deleteBlob(id, "", true);
    else
      getBlob(id, null, true);

    // special transactional tests
    if (!isTransactional)
      return;

    // no operations, commit then close
    tm.begin();
    BlobStoreConnection con = store.openConnection(tm.getTransaction(), null);
    tm.commit();
    con.close();
    assertTrue(con.isClosed());

    // no operations, roll back then close
    tm.begin();
    con = store.openConnection(tm.getTransaction(), null);
    tm.rollback();
    con.close();
    assertTrue(con.isClosed());

    // one operation, commit then close
    tm.begin();
    con = store.openConnection(tm.getTransaction(), null);
    Blob b = getBlob(con, id, null);
    createBlob(con, b, null);
    tm.commit();
    con.close();
    assertTrue(con.isClosed());

    // one operation, roll back then close
    tm.begin();
    con = store.openConnection(tm.getTransaction(), null);
    b = getBlob(con, id, "");
    deleteBlob(con, b);
    tm.rollback();
    con.close();
    assertTrue(con.isClosed());

    // clean up
    deleteBlob(id, "", true);
    assertNoBlobs(getPrefixFor("blobCloseConn"));
  }

  /**
   * Test a closed connection.
   */
  @Test(groups={ "connection" }, dependsOnGroups={ "init" })
  public void testClosedConnection() throws Exception {
    final URI id1 = createId("blobClosedConn1");
    final URI id2 = createId("blobClosedConn2");

    // test con.isClosed() and idempotence of con.close()
    runTests(new Action() {
      public void run(Transaction txn) throws Exception {
        BlobStoreConnection con = store.openConnection(txn, null);
        assertFalse(con.isClosed());

        con.close();
        assertTrue(con.isClosed());

        con.close();
        assertTrue(con.isClosed());

        con.close();
        assertTrue(con.isClosed());
      }
    });

    // test connection operations on a closed connection
    runTests(new Action() {
      public void run(Transaction txn) throws Exception {
        final BlobStoreConnection con = store.openConnection(txn, null);

        for (int idx = 0; idx < 3; idx++) {
          con.close();
          assertTrue(con.isClosed());

          assertEquals(con.getBlobStore(), store);

          shouldFail(new ERunnable() {
            @Override
            public void erun() throws Exception {
              con.getBlob(id1, null);
            }
          }, IllegalStateException.class, null);

          shouldFail(new ERunnable() {
            @Override
            public void erun() throws Exception {
              con.getBlob(null, null);
            }
          }, IllegalStateException.class, null);

          shouldFail(new ERunnable() {
            @Override
            public void erun() throws Exception {
              con.getBlob(new ByteArrayInputStream(new byte[0]), -1, null);
            }
          }, IllegalStateException.class, null);

          if (isListIdsSupp) {
            shouldFail(new ERunnable() {
              @Override
              public void erun() throws Exception {
                con.listBlobIds("foo");
              }
            }, IllegalStateException.class, null);

            shouldFail(new ERunnable() {
              @Override
              public void erun() throws Exception {
                con.listBlobIds(null);
              }
            }, IllegalStateException.class, null);
          }

          if (isSyncSupp) {
            shouldFail(new ERunnable() {
              @Override
              public void erun() throws Exception {
                con.sync();
              }
            }, IllegalStateException.class, null);
          }
        }
      }
    });

    // test non-existent blob operations on a closed connection
    runTests(new Action() {
      public void run(Transaction txn) throws Exception {
        final BlobStoreConnection con = store.openConnection(txn, null);
        final Blob b = getBlob(con, id1, false);

        for (int idx = 0; idx < 3; idx++) {
          con.close();
          assertTrue(con.isClosed());

          assertEquals(b.getConnection(), con);
          assertEquals(b.getId(), id1);

          if (isOutputSupp) {
            shouldFail(new ERunnable() {
              @Override
              public void erun() throws Exception {
                b.openOutputStream(-1, true);
              }
            }, IllegalStateException.class, null);
          }

          shouldFail(new ERunnable() {
            @Override
            public void erun() throws Exception {
              b.exists();
            }
          }, IllegalStateException.class, null);
        }
      }
    });

    // test existing blob operations on a closed connection
    runTests(new Action() {
      public void run(Transaction txn) throws Exception {
        final BlobStoreConnection con = store.openConnection(txn, null);
        final Blob b  = getBlob(con, id1, false);
        final Blob b2 = getBlob(con, id2, false);
        createBlob(con, b, "foo");

        for (int idx = 0; idx < 3; idx++) {
          con.close();
          assertTrue(con.isClosed());

          assertEquals(b.getConnection(), con);
          assertEquals(b.getId(), id1);

          shouldFail(new ERunnable() {
            @Override
            public void erun() throws Exception {
              b.openInputStream();
            }
          }, IllegalStateException.class, null);

          if (isOutputSupp) {
            shouldFail(new ERunnable() {
              @Override
              public void erun() throws Exception {
                b.openOutputStream(-1, true);
              }
            }, IllegalStateException.class, null);
          }

          shouldFail(new ERunnable() {
            @Override
            public void erun() throws Exception {
              b.getSize();
            }
          }, IllegalStateException.class, null);

          shouldFail(new ERunnable() {
            @Override
            public void erun() throws Exception {
              b.exists();
            }
          }, IllegalStateException.class, null);

          if (isDeleteSupp) {
            shouldFail(new ERunnable() {
              @Override
              public void erun() throws Exception {
                b.delete();
              }
            }, IllegalStateException.class, null);
          }

          if (isMoveToSupp) {
            shouldFail(new ERunnable() {
              @Override
              public void erun() throws Exception {
                b.moveTo(b2.getId(), null);
              }
            }, IllegalStateException.class, null);
          }
        }
      }
    });

    // clean up
    deleteBlob(id1, "foo", true);
    assertNoBlobs(getPrefixFor("blobClosedConn"));
  }

  /**
   * Test listing blobs.
   */
  @Test(groups={ "connection", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testListBlobs() throws Exception {
    // check if list-ids is supported
    if (!isListIdsSupp) {
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          con.listBlobIds(null);
        }
      }, UnsupportedOperationException.class, null);

      return;
    }

    // run the tests
    final URI id1 = createId("blobBasicList1");
    final URI id2 = createId("blobBasicList2");

    listBlobs(getPrefixFor("blobBasicList"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicLisT"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicList2"), new URI[] { });

    createBlob(id1, "hello", true);
    listBlobs(getPrefixFor("blobBasicList"), new URI[] { id1 });
    listBlobs(getPrefixFor("blobBasicLisT"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicList2"), new URI[] { });

    createBlob(id2, "bye", true);
    listBlobs(getPrefixFor("blobBasicList"), new URI[] { id1, id2 });
    listBlobs(getPrefixFor("blobBasicLisT"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicList2"), new URI[] { id2 });

    deleteBlob(id1, "hello", true);
    listBlobs(getPrefixFor("blobBasicList"), new URI[] { id2 });
    listBlobs(getPrefixFor("blobBasicLisT"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicList2"), new URI[] { id2 });

    deleteBlob(id2, "bye", true);
    listBlobs(getPrefixFor("blobBasicList"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicLisT"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicList2"), new URI[] { });

    // test that blobs created/deleted as part of the current txn are properly shown
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b1 = getBlob(con, id1, null);
          Blob b2 = getBlob(con, id2, null);

          listBlobs(con, getPrefixFor("blobBasicList"), new URI[] { });
          listBlobs(con, getPrefixFor("blobBasicLisT"), new URI[] { });
          listBlobs(con, getPrefixFor("blobBasicList2"), new URI[] { });

          createBlob(con, b1, "quibledyqwak");
          listBlobs(con, getPrefixFor("blobBasicList"), new URI[] { id1 });
          listBlobs(con, getPrefixFor("blobBasicLisT"), new URI[] { });
          listBlobs(con, getPrefixFor("blobBasicList1"), new URI[] { id1 });

          createBlob(con, b2, "waflebleeblegorm");
          listBlobs(con, getPrefixFor("blobBasicList"), new URI[] { id1, id2 });
          listBlobs(con, getPrefixFor("blobBasicLisT"), new URI[] { });
          listBlobs(con, getPrefixFor("blobBasicList2"), new URI[] { id2 });

          deleteBlob(con, b1);
          listBlobs(con, getPrefixFor("blobBasicList"), new URI[] { id2 });
          listBlobs(con, getPrefixFor("blobBasicLisT"), new URI[] { });
          listBlobs(con, getPrefixFor("blobBasicList2"), new URI[] { id2 });

          deleteBlob(con, b2);
          listBlobs(con, getPrefixFor("blobBasicList"), new URI[] { });
          listBlobs(con, getPrefixFor("blobBasicLisT"), new URI[] { });
          listBlobs(con, getPrefixFor("blobBasicList1"), new URI[] { });
        }
    });
  }

  /**
   * Test sync.
   */
  @Test(groups={ "connection", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testSync() throws Exception {
    // check if sync is supported
    if (!isSyncSupp) {
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          con.sync();
        }
      }, UnsupportedOperationException.class, null);

      return;
    }

    // nothing really to test, other than that it doesn't throw an exception
    final URI id1 = createId("blobConSync1");
    final URI id2 = createId("blobConSync2");

    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          con.sync();
        }
    });

    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id1, null);
          createBlob(con, b, "foos");
          con.sync();
          deleteBlob(con, b);
        }
    });

    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id1, null);
          createBlob(con, b, "foos");
          deleteBlob(con, b);
          con.sync();
        }
    });

    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id1, null);
          createBlob(con, b, "foos");
          b = moveBlob(con, b, id2, "foos");
          con.sync();
          deleteBlob(con, b);
        }
    });

    assertNoBlobs(getPrefixFor("blobConSync"));
  }


  /*
   * Blob tests.
   */

  /**
   * Test id validation.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testIdValidation() throws Exception {
    // valid should work
    URI valId = createId("blobValidId");

    createBlob(valId, null, true);
    deleteBlob(valId, "", true);

    // invalid should not
    final URI invId = getInvalidId();
    if (invId != null) {
      shouldFail(new ERunnable() {
        @Override
        public void erun() throws Exception {
          createBlob(invId, null, true);
        }
      }, UnsupportedIdException.class, invId);
    }
  }

  /**
   * Test id generation.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testIdGeneration() throws Exception {
    // check if id-gen is supported
    if (!isIdGenSupp) {
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          getBlob(con, null, false);
        }
      }, UnsupportedOperationException.class, null);

      return;
    }

    // null id should work
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, null, false);
          if (b.exists())
            deleteBlob(con, b);
        }
    }, false);
  }

  /**
   * Test canonical-id.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testCanonicalId() throws Exception {
    // set up
    final URI   id      = createId("blobCanonicalId");
    final URI[] aliases = getAliases(id);

    // test
    runTests(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        if (aliases == null) {
          Blob b = getBlob(con, id, false);
          assertNull(b.getCanonicalId());
        } else {
          for (URI alias : aliases) {
            Blob b = getBlob(con, alias, false);
            assertEquals(b.getCanonicalId(), id);
          }
        }
      }
    });
  }

  /**
   * Test non-existent blob.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testNonExistentBlob() throws Exception {
    final URI id = createId("blobNonExistent1");

    runTests(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        Blob b = getBlob(con, id, false);
        assertEquals(b.getConnection(), con);
        assertEquals(b.getId(), id);
      }
    });

    shouldFail(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        getBlob(con, id, false).getSize();
      }
    }, MissingBlobException.class, id);

    shouldFail(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        getBlob(con, id, false).openInputStream().close();
      }
    }, MissingBlobException.class, id);
  }

  /**
   * Test con.getBlob(InputStream, ...).
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testBlobFromStream() throws Exception {
    // check if id-gen is supported
    if (!isIdGenSupp) {
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          con.getBlob(new ByteArrayInputStream(new byte[0]), -1, null);
        }
      }, UnsupportedOperationException.class, null);

      return;
    }

    // null stream should fail
    shouldFail(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        con.getBlob(null, -1, null);
      }
    }, NullPointerException.class, null);

    // basic input-stream should work
    testBlobFromStream("", -1);
    testBlobFromStream("", 0);
    testBlobFromStream("", 10);

    testBlobFromStream("Tyrant, remember?", -1);
    testBlobFromStream("Tyrant, remember?", 0);
    testBlobFromStream("Tyrant, remember?", 10);
    testBlobFromStream("Tyrant, remember?", 17);
    testBlobFromStream("Tyrant, remember?", 27);
  }

  protected void testBlobFromStream(final String body, final long estSize) throws Exception {
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = con.getBlob(new ByteArrayInputStream(body.getBytes("UTF-8")), estSize, null);
          assertTrue(b.exists());
          assertTrue(con.getBlob(b.getId(), null).exists());

          assertEquals(getBody(b), body);
          assertEquals(getBody(con.getBlob(b.getId(), null)), body);

          deleteBlob(con, b);
        }
    });
  }

  /**
   * Test id validation.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testDelete() throws Exception {
    // set up
    final URI id1 = createId("blobDelete1");
    createBlob(id1, null, true);

    // check if delete is supported
    if (!isDeleteSupp) {
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id1, "");
          b.delete();
        }
      }, UnsupportedOperationException.class, null);

      getBlob(id1, "", true);   // make sure it's still exists

      deleteBlob(id1, "", true);
      return;
    }

    // delete of an existing blob
    deleteBlob(id1, "", true);

    // delete of a non-existent blob should succeed
    deleteBlob(id1, null, true);

    // clean up
    assertNoBlobs(getPrefixFor("blobDelete"));
  }

  /**
   * Test openInputStream.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testInputStream() throws Exception {
    // set up
    final URI id1 = createId("blobInputStream1");

    // openInputStream on non-existent blob
    shouldFail(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        Blob b = getBlob(con, id1, false);
        b.openInputStream().close();
      }
    }, MissingBlobException.class, id1);

    // openInputStream on existing blob
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          String body = "For the love of God, Montresor";
          Blob b = getBlob(con, id1, null);
          createBlob(con, b, body);

          // read partial body
          byte[] buf = new byte[100];

          InputStream is = b.openInputStream();
          int got = is.read(buf, 0, 10);
          assertTrue(got > 0 && got <= 10, "Invalid number of bytes read: " + got);
          int len = got;
          is.close();
          assertEquals(new String(buf, 0, len, "UTF-8"), body.substring(0, len));

          is = b.openInputStream();
          got = 0;
          while (got < len) {
            int skipped = (int) is.skip(len - got);
            assertTrue(skipped > 0 && skipped <= (len - got),
                       "Invalid number of bytes skipped: " + skipped);
            got += skipped;
          }

          got = is.read(buf, len, 2);
          assertTrue(got > 0 && got <= 2, "Invalid number of bytes read: " + got);
          len += got;
          got = is.read(buf, len, 5);
          assertTrue(got > 0 && got <= 5, "Invalid number of bytes read: " + got);
          len += got;
          is.close();
          assertEquals(new String(buf, 0, len, "UTF-8"), body.substring(0, len));

          // read whole body
          assertEquals(getBody(b), body);

          deleteBlob(con, b);
        }
    });

    // clean up
    assertNoBlobs(getPrefixFor("blobInputStream"));
  }

  /**
   * Test openOutputStream.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testOutputStream() throws Exception {
    // check if create is supported
    if (!isOutputSupp) {
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, createId("blobOutputStream1"), false);
          b.openOutputStream(-1, true).close();
        }
      }, UnsupportedOperationException.class, null);

      return;
    }

    // set up
    final URI id1 = createId("blobOutputStream1");

    // 0 length blob, no write
    testOutputStream(id1, null, "a", -1);
    testOutputStream(id1, null, "a", 0);
    testOutputStream(id1, null, "a", 20);

    // 0 length blob, empty write
    testOutputStream(id1, "", "a", -1);
    testOutputStream(id1, "", "a", 0);
    testOutputStream(id1, "", "a", 20);

    // >0 length blob
    testOutputStream(id1, "foo bar", "a", -1);
    testOutputStream(id1, "foo bar", "a", 0);
    testOutputStream(id1, "foo bar", "a", 5);
    testOutputStream(id1, "foo bar", "a", 7);
    testOutputStream(id1, "foo bar", "a", 17);

    // clean up
    assertNoBlobs(getPrefixFor("blobOutputStream"));
  }

  protected void testOutputStream(final URI id, final String body, final String body2,
                                  final long estSize) throws Exception {
    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          final Blob b = getBlob(con, id, null);

          testOutputStream(b, body, estSize, true);     // test create
          testOutputStream(b, body2, estSize, true);    // test overwrite

          deleteBlob(con, b);

          testOutputStream(b, body, estSize, false);    // test create
          shouldFail(new ERunnable() {                  // test !overwrite
            @Override
            public void erun() throws Exception {
              testOutputStream(b, body2, estSize, false);
            }
          }, DuplicateBlobException.class, id);

          assertEquals(getBody(b), body != null ? body : "");

          deleteBlob(con, b);
        }
    });
  }

  protected void testOutputStream(Blob b, String body, long estSize, boolean overwrite)
      throws Exception {
    if (body != null) {
      setBody(b, body, estSize, overwrite);
      assertEquals(getBody(b), body);
    } else {
      b.openOutputStream(estSize, overwrite).close();
      assertEquals(getBody(b), "");
    }
  }

  /**
   * Test changing a blob's value.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testBlobUpdate() throws Exception {
    final URI id = createId("blobBlobUpdate1");

    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, null);
          createBlob(con, b, null);
          setBlob(con, b, "value1");
          setBlob(con, b, "value2");
          setBlob(con, b, "value3");
        }
    }, true);

    runTests(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, "value3");
          setBlob(con, b, "value4");
          setBlob(con, b, "value5");
        }
    }, true);

    deleteBlob(id, "value5", true);
    getBlob(id, null, true);

    assertNoBlobs(getPrefixFor("blobBlobUpdate"));
  }

  /**
   * Test move.
   */
  @Test(groups={ "blob", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testMoveTo() throws Exception {
    // check if move is supported
    if (!isMoveToSupp) {
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, createId("blobMoveTo1"), false);
          b.moveTo(b.getId(), null);
        }
      }, UnsupportedOperationException.class, null);

      return;
    }

    // set up
    final URI id1 = createId("blobMoveTo1");
    final URI id2 = createId("blobMoveTo2");
    final URI id3 = createId("blobMoveTo3");
    final URI id4 = createId("blobMoveTo4");

    createBlob(id1, "foo", true);
    createBlob(id4, "bar", true);

    // move blob from id1 to id2
    renameBlob(id1, id2, "foo", true);

    // move from non-existent blob should fail
    shouldFail(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        Blob ob = getBlob(con, id1, false);
        ob.moveTo(id3, null);
      }
    }, MissingBlobException.class, id1);

    getBlob(id1, null, true);
    getBlob(id3, null, true);

    // move to existing blob should fail
    shouldFail(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        Blob ob = getBlob(con, id2, "foo");
        ob.moveTo(id4, null);
      }
    }, DuplicateBlobException.class, id4);

    getBlob(id2, "foo", true);
    getBlob(id4, "bar", true);

    // move a non-existent blob onto itself should fail
    shouldFail(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        Blob b = getBlob(con, id1, false);
        b.moveTo(id1, null);
      }
    }, MissingBlobException.class, id1);

    getBlob(id1, null, true);

    // move an existing blob onto itself should fail
    shouldFail(new ConAction() {
      public void run(BlobStoreConnection con) throws Exception {
        Blob b = getBlob(con, id2, "foo");
        b.moveTo(id2, null);
      }
    }, DuplicateBlobException.class, id2);

    getBlob(id2, "foo", true);

    // move to null
    if (!isIdGenSupp) {
      // move to null should fail
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id2, "foo");
          b.moveTo(null, null);
        }
      }, UnsupportedOperationException.class, null);

      getBlob(id2, "foo", true);
    } else {
      // null id should work
      runTests(new ConAction() {
          public void run(BlobStoreConnection con) throws Exception {
            Blob b = getBlob(con, id2, "foo");
            Blob b2 = b.moveTo(null, null);
            // undo for other tests
            b2.moveTo(id2, null);
          }
      }, false);
    }

    // move to incompatible blob should fail
    final URI inv = getInvalidId();
    if (inv != null) {
      shouldFail(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob ob = getBlob(con, id2, "foo");
          ob.moveTo(inv, null);
        }
      }, UnsupportedIdException.class, inv);
    }

    getBlob(id2, "foo", true);

    // clean up
    deleteBlob(id2, "foo", true);
    deleteBlob(id4, "bar", true);

    assertNoBlobs(getPrefixFor("blobMoveTo"));
  }

  /*
   * Transaction tests.
   */

  /**
   * Basic create, get, rename, delete.
   */
  @Test(groups={ "transaction", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testBasicCommit() throws Exception {
    if (!isTransactional)
      return;

    URI id = createId("blobBasicCommit1");

    createBlob(id, "hello", true);
    getBlob(id, "hello", true);

    if (isMoveToSupp) {
      URI id2 = createId("blobBasicCommit2");
      renameBlob(id, id2, "hello", true);
      getBlob(id, null, true);
      getBlob(id2, "hello", true);

      id = id2;
    }

    setBlob(id, "bye bye", true);
    getBlob(id, "bye bye", true);

    deleteBlob(id, "bye bye", true);
    getBlob(id, null, true);

    assertNoBlobs(getPrefixFor("blobBasicCommit"));
  }

  /**
   * Basic create, get, rename, delete with rollbacks.
   */
  @Test(groups={ "transaction", "manipulatesBlobs" }, dependsOnGroups={ "init" })
  public void testBasicRollback() throws Exception {
    if (!isTransactional)
      return;

    URI id = createId("blobBasicRollback1");

    // roll back a create
    createBlob(id, "hello", false);
    getBlob(id, null, false);

    // create, roll back a rename
    createBlob(id, "hello", true);
    getBlob(id, "hello", true);

    if (isMoveToSupp) {
      URI id2 = createId("blobBasicRollback2");
      renameBlob(id, id2, "hello", false);

      getBlob(id2, null, true);
    }

    getBlob(id, "hello", true);

    // update and roll back
    setBlob(id, "bye bye", false);
    getBlob(id, "hello", true);

    // roll back a delete
    deleteBlob(id, "hello", false);
    getBlob(id, "hello", true);

    // delete
    deleteBlob(id, "hello", true);
    getBlob(id, null, true);

    assertNoBlobs(getPrefixFor("blobBasicRollback"));
  }


  /**
   * Test that things get cleaned up. This runs after all other tests that create or otherwise
   * manipulate blobs.
   */
  @Test(groups={ "post" }, dependsOnGroups={ "manipulatesBlobs" })
  public void testCleanup() throws Exception {
    assertNoBlobs(null);
    assertNoBlobs("");
  }
}
