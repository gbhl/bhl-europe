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

package org.akubraproject.txn.derby;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.mem.MemBlobStore;
import org.akubraproject.tck.TCKTestSuite;
import org.akubraproject.txn.ConcurrentBlobUpdateException;

/**
 * Unit tests for {@link TransactionalStore}.
 *
 * @author Ronald Tschalär
 */
public class TestTransactionalStore extends TCKTestSuite {
  private final File      dbDir;
  private final boolean   singleWriter;

  public TestTransactionalStore() throws Exception {
    super(getStore(), getStoreId(), true, false);

    dbDir        = getDbDir();
    singleWriter = ((TransactionalStore) store).singleWriter();

    /*
    java.util.logging.LogManager.getLogManager().readConfiguration(
        new java.io.FileInputStream("/tmp/jdklog.properties"));
    */
  }

  private static URI getStoreId() {
    return URI.create("urn:example:txnstore");
  }

  private static File getDbDir() throws Exception {
    File base = new File(System.getProperty("basedir"), "target");
    return new File(base, "txn-db");
  }

  private static TransactionalStore getStore() throws Exception {
    File dbDir = getDbDir();
    FileUtils.deleteDirectory(dbDir);
    dbDir.getParentFile().mkdirs();

    File base = new File(System.getProperty("basedir"), "target");
    System.setProperty("derby.stream.error.file", new File(base, "derby.log").toString());
    return new TransactionalStore(getStoreId(), new MemBlobStore(), dbDir.getAbsolutePath());
  }

  @Override
  protected URI getInvalidId() {
    // one too long
    StringBuilder uri = new StringBuilder("urn:blobIdValidation");
    for (int idx = 0; idx < 98; idx++)
      uri.append("oooooooooo");
    uri.append("x");

    return URI.create(uri.toString() + "x");
  }

  /** all URI's are distinct */
  @Override
  protected URI[] getAliases(URI uri) {
    return new URI[] { uri };
  }

  @Override
  public void testListBlobs() throws Exception {
    super.testListBlobs();

    // test when there are old versions too
    URI id1 = createId("blobBasicList1");
    URI id2 = createId("blobBasicList2");
    createBlob(id1, "hello", true);
    createBlob(id2, "bye", true);

    final boolean[] cv = new boolean[] { false };
    doInThread(new ERunnable() {
      @Override
      public void erun() throws Exception {
        doInTxn(new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              waitFor(cv, true, 0);
            }
        }, false);
      }
    });

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

    createBlob(id1, "hello2", true);
    listBlobs(getPrefixFor("blobBasicList"), new URI[] { id1 });
    listBlobs(getPrefixFor("blobBasicLisT"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicList1"), new URI[] { id1 });

    deleteBlob(id1, "hello2", true);
    listBlobs(getPrefixFor("blobBasicList"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicLisT"), new URI[] { });
    listBlobs(getPrefixFor("blobBasicList1"), new URI[] { });

    notify(cv, true);
  }

  /**
   * Test deletions are done and cleaned up properly under various combinations of
   * creating/moving/deleting blobs.
   */
  @Test(groups={ "blobs" }, dependsOnGroups={ "init" })
  public void testDeleteCleanup() throws Exception {
    final URI    id   = URI.create("urn:blobBlobDelete1");
    final URI    id2  = URI.create("urn:blobBlobDelete2");
    final String body = "value";

    // create-delete in one txn
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, null);
          createBlob(con, b, null);
          deleteBlob(con, b);
        }
    }, true);

    // create-delete-create in one txn
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, null);
          createBlob(con, b, null);
          deleteBlob(con, b);
          createBlob(con, b, null);
        }
    }, true);

    // delete-create-delete in one txn
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, "");
          deleteBlob(con, b);
          createBlob(con, b, null);
          deleteBlob(con, b);
        }
    }, true);

    // create-delete-create-delete in one txn
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, null);
          createBlob(con, b, null);
          deleteBlob(con, b);
          createBlob(con, b, null);
          deleteBlob(con, b);
        }
    }, true);

    // create-update-delete-create-update-delete in one txn
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, null);
          createBlob(con, b, body);
          deleteBlob(con, b);
          createBlob(con, b, body);
          deleteBlob(con, b);
        }
    }, true);

    // create in one, delete in another
    createBlob(id, body, true);
    deleteBlob(id, body, true);

    // create in one, delete + create in another
    createBlob(id, body, true);

    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, body);
          deleteBlob(con, b);
          createBlob(con, b, null);
        }
    }, true);

    // delete + create + update in one
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, "");
          deleteBlob(con, b);
          createBlob(con, b, body);
        }
    }, true);

    // update + delete + create + update in one
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, body);
          setBlob(con, b, "foo");
          deleteBlob(con, b);
          createBlob(con, b, body);
        }
    }, true);

    // delete + create + update + delete in one
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b = getBlob(con, id, body);
          deleteBlob(con, b);
          createBlob(con, b, body);
          deleteBlob(con, b);
        }
    }, true);

    // create-move-delete in one txn
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b  = getBlob(con, id, null);
          Blob b2 = getBlob(con, id2, null);

          createBlob(con, b, null);
          moveBlob(con, b, id2, "");
          deleteBlob(con, b2);
        }
    }, true);

    // create in one, move-delete in another txn
    createBlob(id, body, true);

    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b  = getBlob(con, id, body);
          Blob b2 = getBlob(con, id2, null);

          moveBlob(con, b, id2, body);
          deleteBlob(con, b2);
        }
    }, true);

    // create-move-delete-create-move in one txn
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b  = getBlob(con, id, null);
          Blob b2 = getBlob(con, id2, null);

          createBlob(con, b, null);
          moveBlob(con, b, id2, "");
          deleteBlob(con, b2);
          createBlob(con, b2, null);
          moveBlob(con, b2, id, "");
          setBlob(con, b, body);
        }
    }, true);

    // move-delete-create-move-again-yada-yada...
    doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          Blob b  = getBlob(con, id, body);
          Blob b2 = getBlob(con, id2, null);

          moveBlob(con, b, id2, body);
          deleteBlob(con, b2);
          createBlob(con, b, null);
          moveBlob(con, b, id2, "");
          moveBlob(con, b2, id, "");
          deleteBlob(con, b);
          createBlob(con, b2, null);
          moveBlob(con, b2, id, "");
          setBlob(con, b, body);
          moveBlob(con, b, id2, body);
          deleteBlob(con, b2);
          createBlob(con, b, body);
        }
    }, true);

    // clean up
    deleteBlob(id, body, true);
    getBlob(id, null, true);

    assertNoBlobs("urn:blobBlobDelete");
  }

  /**
   * Test conflicts between two transactions (creating, updating, or deleting a blob that
   * the other has touched).
   */
  @Test(groups={ "blobs" }, dependsOnGroups={ "init" })
  public void testConflicts() throws Exception {
    if (singleWriter)
      return;

    final URI id1 = URI.create("urn:blobConflict1");
    final URI id2 = URI.create("urn:blobConflict2");
    final String body1  = "original blob";
    final String body11 = "modified blob";
    final String body2  = "create me";
    // create blob1
    createBlob(id1, body1, true);

    // create a set of actions
    ConAction createNoBody = new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              Blob b = getBlob(con, id2, null);
              createBlob(con, b, null);
            }
        };

    ConAction createWithBody = new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              Blob b = getBlob(con, id2, null);
              createBlob(con, b, body2);
            }
        };

    ConAction delete1 = new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              Blob b = getBlob(con, id1, body1);
              deleteBlob(con, b);
            }
        };

    ConAction modify1 = new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              Blob b = getBlob(con, id1, body1);
              setBlob(con, b, body11);
            }
        };

    ConAction rename12 = new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              Blob b1 = getBlob(con, id1, body1);
              moveBlob(con, b1, id2, body1);
            }
        };

    // test create-create conflict
    testConflict(createNoBody, createNoBody, id2, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id2, "", true);
        deleteBlob(id2, "", true);
      }
    });

    testConflict(createWithBody, createNoBody, id2, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id2, body2, true);
        deleteBlob(id2, body2, true);
      }
    });

    testConflict(createWithBody, createWithBody, id2, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id2, body2, true);
        deleteBlob(id2, body2, true);
      }
    });

    testConflict(createNoBody, createWithBody, id2, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id2, "", true);
        deleteBlob(id2, "", true);
      }
    });

    // test delete-delete conflict
    testConflict(delete1, delete1, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id1, null, true);
        createBlob(id1, body1, true);
      }
    });

    // test delete-modify conflict
    testConflict(delete1, modify1, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id1, null, true);
        createBlob(id1, body1, true);
      }
    });

    testConflict(modify1, delete1, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id1, body11, true);
        setBlob(id1, body1, true);
      }
    });

    // test modify-modify conflict
    testConflict(modify1, modify1, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id1, body11, true);
        setBlob(id1, body1, true);
      }
    });

    // test rename-rename conflict
    testConflict(rename12, rename12, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id2, body1, true);
        renameBlob(id2, id1, body1, true);
      }
    });

    // test rename-modify conflict
    testConflict(rename12, modify1, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id2, body1, true);
        renameBlob(id2, id1, body1, true);
      }
    });

    testConflict(modify1, rename12, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id1, body11, true);
        setBlob(id1, body1, true);
      }
    });

    // test rename-create conflict
    testConflict(rename12, createNoBody, id2, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id2, body1, true);
        renameBlob(id2, id1, body1, true);
      }
    });

    testConflict(createNoBody, rename12, id2, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id1, body1, true);
        getBlob(id2, "", true);
        deleteBlob(id2, "", true);
      }
    });

    testConflict(createWithBody, rename12, id2, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id1, body1, true);
        getBlob(id2, body2, true);
        deleteBlob(id2, body2, true);
      }
    });

    // test rename-delete conflict
    testConflict(rename12, delete1, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id2, body1, true);
        renameBlob(id2, id1, body1, true);
      }
    });

    testConflict(delete1, rename12, id1, new ERunnable() {
      @Override
      public void erun() throws Exception {
        getBlob(id1, null, true);
        createBlob(id1, body1, true);
      }
    });

    // clean up

    deleteBlob(id1, body1, true);
    getBlob(id1, null, true);

    assertNoBlobs("urn:blobConflict");
  }

  private void testConflict(final ConAction first, final ConAction second, final URI id,
                            final ERunnable reset) throws Exception {
    final boolean[] cv     = new boolean[] { false };
    final boolean[] failed = new boolean[] { false };
    Thread[] threads = new Thread[2];

    // Test two threads, both operations occurring while the other hasn't committed yet
    threads[0] = doInThread(new ERunnable() {
      @Override
      public void erun() throws Exception {
        doInTxn(new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              notifyAndWait(cv, true);

              first.run(con);

              notifyAndWait(cv, true);
            }
        }, true);

        TestTransactionalStore.notify(cv, true);
      }
    }, failed);

    threads[1] = doInThread(new ERunnable() {
      @Override
      public void erun() throws Exception {
        doInTxn(new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              waitFor(cv, true, 0);
              notifyAndWait(cv, false);

              try {
                second.run(con);
                fail("Did not get expected ConcurrentBlobUpdateException");
              } catch (ConcurrentBlobUpdateException cbue) {
                assertEquals(cbue.getBlobId(), id);
              }

              notifyAndWait(cv, false);
            }
        }, false);
      }
    }, failed);

    for (int t = 0; t < threads.length; t++)
      threads[t].join();

    assertFalse(failed[0]);

    reset.erun();

    /* Test two threads, both starting, then the first doing its operation and comitting, then the
     * second doing its operation.
     */
    cv[0] = false;

    threads[0] = doInThread(new ERunnable() {
      @Override
      public void erun() throws Exception {
        notifyAndWait(cv, true);

        doInTxn(new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              notifyAndWait(cv, true);
              first.run(con);
            }
        }, true);

        TestTransactionalStore.notify(cv, true);
      }
    }, failed);

    threads[1] = doInThread(new ERunnable() {
      @Override
      public void erun() throws Exception {
        waitFor(cv, true, 0);
        notifyAndWait(cv, false);

        doInTxn(new ConAction() {
            public void run(BlobStoreConnection con) throws Exception {
              notifyAndWait(cv, false);
              try {
                second.run(con);
                fail("Did not get expected ConcurrentBlobUpdateException");
              } catch (ConcurrentBlobUpdateException cbue) {
                assertEquals(cbue.getBlobId(), id);
              }
            }
        }, false);
      }
    }, failed);

    for (int t = 0; t < threads.length; t++)
      threads[t].join();

    assertFalse(failed[0]);

    reset.erun();
  }

  /**
   * Create, get, rename, update, delete in other transactions should not affect current
   * transaction.
   */
  @Test(groups={ "blobs" }, dependsOnGroups={ "init" })
  public void testBasicTransactionIsolation() throws Exception {
    if (singleWriter)
      return;

    final URI id1 = URI.create("urn:blobBasicTxnIsol1");
    final URI id2 = URI.create("urn:blobBasicTxnIsol2");
    final URI id3 = URI.create("urn:blobBasicTxnIsol3");
    final URI id4 = URI.create("urn:blobBasicTxnIsol4");

    final String body1  = "long lived blob";
    final String body2  = "long lived, modified blob";
    final String body3  = "create me";
    final String body4  = "delete me";
    final String body22 = "body1, v2";
    final String body42 = "delete me, v2";
    final String body43 = "delete me, v3";

    // create blob1
    createBlob(id1, body1, true);

    // first start txn1, then run a bunch of other transactions while txn1 is active
    doInTxn(new ConAction() {
        public void run(final BlobStoreConnection con) throws Exception {
          // check our snapshot
          getBlob(con, id1, body1);
          getBlob(con, id2, null);
          getBlob(con, id3, null);
          getBlob(con, id4, null);

          // create another blob and modify
          Blob b = getBlob(con, id2, null);
          createBlob(con, b, body1);
          setBlob(con, b, body2);

          // create a new blob and verify we don't see it but others see it
          boolean[] failed = new boolean[] { false };

          doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              createBlob(id3, body3, true);
            }
          }, failed).join();
          assertFalse(failed[0]);

          doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              getBlob(id1, body1, true);
              getBlob(id2, null, true);
              getBlob(id3, body3, true);
              getBlob(id4, null, true);
            }
          }, failed).join();
          assertFalse(failed[0]);

          getBlob(con, id1, body1);
          getBlob(con, id2, body2);
          getBlob(con, id3, null);
          getBlob(con, id4, null);

          // delete the new blob
          doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              deleteBlob(id3, body3, true);
            }
          }, failed).join();
          assertFalse(failed[0]);

          doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              getBlob(id1, body1, true);
              getBlob(id2, null, true);
              getBlob(id3, null, true);
              getBlob(id4, null, true);
            }
          }, failed).join();
          assertFalse(failed[0]);

          getBlob(con, id1, body1);
          getBlob(con, id2, body2);
          getBlob(con, id3, null);
          getBlob(con, id4, null);

          // delete the first blob
          doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              deleteBlob(id1, body1, true);
            }
          }, failed).join();
          assertFalse(failed[0]);

          doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              getBlob(id1, null, true);
              getBlob(id2, null, true);
              getBlob(id3, null, true);
              getBlob(id4, null, true);
            }
          }, failed).join();
          assertFalse(failed[0]);

          getBlob(con, id1, body1);
          getBlob(con, id2, body2);
          getBlob(con, id3, null);
          getBlob(con, id4, null);

          // re-create the first blob, but with different content
          doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              createBlob(id1, body22, true);
            }
          }, failed).join();
          assertFalse(failed[0]);

          doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              getBlob(id1, body22, true);
              getBlob(id2, null, true);
              getBlob(id3, null, true);
              getBlob(id4, null, true);
            }
          }, failed).join();
          assertFalse(failed[0]);

          getBlob(con, id1, body1);
          getBlob(con, id2, body2);
          getBlob(con, id3, null);
          getBlob(con, id4, null);

          // step through, making sure we don't see anything from active transactions
          final boolean[] cv = new boolean[1];
          Thread t = doInThread(new ERunnable() {
            @Override
            public void erun() throws Exception {
              doInTxn(new ConAction() {
                public void run(BlobStoreConnection c2) throws Exception {
                  Blob b = getBlob(c2, id4, null);
                  createBlob(c2, b, body4);

                  notifyAndWait(cv, true);

                  deleteBlob(c2, b);

                  notifyAndWait(cv, true);

                  createBlob(c2, b, body42);

                  notifyAndWait(cv, true);

                  setBlob(c2, b, body43);

                  notifyAndWait(cv, true);
                }
              }, true);
            }
          }, failed);

          waitFor(cv, true, 0);

          assertFalse(con.getBlob(id4, null).exists());

          notifyAndWait(cv, false);

          assertFalse(con.getBlob(id4, null).exists());

          notifyAndWait(cv, false);

          assertFalse(con.getBlob(id4, null).exists());

          notifyAndWait(cv, false);

          assertFalse(con.getBlob(id4, null).exists());

          TestTransactionalStore.notify(cv, false);
          t.join();
          assertFalse(failed[0]);

          assertFalse(con.getBlob(id4, null).exists());
        }
    }, true);

    deleteBlob(id1, body22, true);
    deleteBlob(id2, body2, true);
    deleteBlob(id3, null, true);
    deleteBlob(id4, body43, true);

    assertNoBlobs("urn:blobBasicTxnIsol");
  }

  /**
   * Test single stepping two transactions.
   */
  @Test(groups={ "blobs" }, dependsOnGroups={ "init" })
  public void testTransactionIsolation2() throws Exception {
    if (singleWriter)
      return;

    final URI id1 = URI.create("urn:blobTxnIsol2_1");
    final URI id2 = URI.create("urn:blobTxnIsol2_2");
    final URI id3 = URI.create("urn:blobTxnIsol2_3");
    final URI id4 = URI.create("urn:blobTxnIsol2_4");
    final URI id5 = URI.create("urn:blobTxnIsol2_5");
    final URI id6 = URI.create("urn:blobTxnIsol2_6");

    final String body1  = "blob1";
    final String body2  = "blob2";
    final String body3  = "blob3";
    final String body4  = "blob4";

    final boolean[] failed  = new boolean[] { false };
    final boolean[] cv      = new boolean[] { false };
    final Thread[]  threads = new Thread[2];

    // 2 threads, which will single-step, each doing a step and then waiting for the other
    for (int t = 0; t < threads.length; t++) {
      final URI[]    ids     = new URI[]    { t == 0 ? id1 : id3, t == 0 ? id2 : id4 };
      final URI[]    oids    = new URI[]    { t != 0 ? id1 : id3, t != 0 ? id2 : id4 };
      final URI      tid     = t == 0 ? id5 : id6;
      final String[] bodies  = new String[] { t == 0 ? body1 : body3, t == 0 ? body2 : body4 };
      final String[] obodies = new String[] { t != 0 ? body1 : body3, t != 0 ? body2 : body4 };
      final boolean  cvVal   = (t == 0);

      threads[t] = doInThread(new ERunnable() {
        @Override
        public void erun() throws Exception {
          // create two blobs
          doInTxn(new ConAction() {
              public void run(final BlobStoreConnection con) throws Exception {
                getBlob(con, id1, false);
                getBlob(con, id2, false);
                getBlob(con, id3, false);
                getBlob(con, id4, false);

                waitFor(cv, cvVal, 0);

                notifyAndWait(cv, !cvVal);

                for (int idx = 0; idx < 2; idx++ ) {
                  Blob b = getBlob(con, ids[idx], null);
                  createBlob(con, b, null);

                  for (int idx2 = 0; idx2 < 2; idx2++ )
                    getBlob(con, oids[idx2], false);

                  notifyAndWait(cv, !cvVal);

                  setBlob(con, b, bodies[idx]);

                  notifyAndWait(cv, !cvVal);
                }
              }
          }, true);

          notifyAndWait(cv, !cvVal);

          // exchange the two blobs
          doInTxn(new ConAction() {
              public void run(final BlobStoreConnection con) throws Exception {
                getBlob(con, id1, body1);
                getBlob(con, id2, body2);
                getBlob(con, id3, body3);
                getBlob(con, id4, body4);

                notifyAndWait(cv, !cvVal);

                URI[][] seq = new URI[][] {
                  { ids[0], tid },
                  { ids[1], ids[0] },
                  { tid, ids[1] },
                };

                for (int idx = 0; idx < seq.length; idx++ ) {
                  Blob bs = getBlob(con, seq[idx][0], true);
                  moveBlob(con, bs, seq[idx][1], null);

                  notifyAndWait(cv, !cvVal);
                }
              }
          }, true);

          notifyAndWait(cv, !cvVal);

          // delete the two blobs
          doInTxn(new ConAction() {
              public void run(final BlobStoreConnection con) throws Exception {
                getBlob(con, id1, body2);
                getBlob(con, id2, body1);
                getBlob(con, id3, body4);
                getBlob(con, id4, body3);

                notifyAndWait(cv, !cvVal);

                for (int idx = 0; idx < 2; idx++ ) {
                  Blob b = getBlob(con, ids[idx], bodies[1 - idx]);
                  deleteBlob(con, b);

                  for (int idx2 = 0; idx2 < 2; idx2++ )
                    getBlob(con, oids[idx2], obodies[1 - idx2]);

                  notifyAndWait(cv, !cvVal);
                }
              }
          }, true);

          notifyAndWait(cv, !cvVal);
          TestTransactionalStore.notify(cv, !cvVal);
        }
      }, failed);
    }

    for (int t = 0; t < threads.length; t++)
      threads[t].join();

    assertFalse(failed[0]);

    assertNoBlobs("urn:blobTxnIsol2_");
  }

  /**
   * Stress test the stuff a bit.
   */
  @Test(groups={ "blobs" }, dependsOnGroups={ "init" })
  public void stressTest() throws Exception {
    // get our config
    final int numFillers = Integer.getInteger("akubra.txn.test.numFillers", 0);
    final int numReaders = Integer.getInteger("akubra.txn.test.numReaders", 10);
    final int numWriters = Integer.getInteger("akubra.txn.test.numWriters", 10);
    final int numObjects = Integer.getInteger("akubra.txn.test.numObjects", 10);
    final int numRounds  = Integer.getInteger("akubra.txn.test.numRounds",  10);

    long t0 = System.currentTimeMillis();

    // "fill" the db a bit
    for (int b = 0; b < numFillers / 1000; b++) {
      final int start = b * 1000;

      doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          for (int idx = start; idx < start + 1000; idx++) {
            Blob b = con.getBlob(URI.create("urn:blobStressTestFiller" + idx), null);
            setBody(b, "v" + idx);
          }
        }
      }, true);
    }

    long t1 = System.currentTimeMillis();

    // set up
    Thread[] writers = new Thread[numWriters];
    Thread[] readers = new Thread[numReaders];
    boolean[] failed = new boolean[] { false };

    final boolean[] testDone = new boolean[] { false };
    final int[]     lowIds   = new int[numWriters];
    final int[]     highId   = new int[] { 0 };

    // start/run the writers
    for (int t = 0; t < writers.length; t++) {
      final int tid   = t;
      final int start = t * numRounds * numObjects;

      writers[t] = doInThread(new ERunnable() {
        @Override
        public void erun() throws Exception {
          for (int r = 0; r < numRounds; r++) {
            final int off = start + r * numObjects;

            doInTxn(new ConAction() {
              public void run(BlobStoreConnection con) throws Exception {
                for (int o = 0; o < numObjects; o++) {
                  int    idx = off + o;
                  URI    id  = URI.create("urn:blobStressTest" + idx);
                  String val = "v" + idx;

                  Blob b = getBlob(con, id, null);
                  createBlob(con, b, val);
                }
              }
            }, true);

            synchronized (testDone) {
              highId[0] = Math.max(highId[0], off + numObjects);
            }

            doInTxn(new ConAction() {
              public void run(BlobStoreConnection con) throws Exception {
                for (int o = 0; o < numObjects; o++) {
                  int    idx = off + o;
                  URI    id  = URI.create("urn:blobStressTest" + idx);
                  String val = "v" + idx;

                  Blob b = getBlob(con, id, val);
                  deleteBlob(con, b);
                }
              }
            }, true);

            synchronized (testDone) {
              lowIds[tid] = off + numObjects;
            }
          }
        }
      }, failed);
    }

    // start/run the readers
    for (int t = 0; t < readers.length; t++) {
      readers[t] = doInThread(new ERunnable() {
        @Override
        public void erun() throws Exception {
          final Random rng   = new Random();
          final int[]  found = new int[] { 0 };

          while (true) {
            final int low, high;
            synchronized (testDone) {
              if (testDone[0])
                break;

              high = highId[0];

              int tmp = Integer.MAX_VALUE;
              for (int id : lowIds)
                tmp = Math.min(tmp, id);
              low = tmp;
            }

            if (low == high) {
              Thread.yield();
              continue;
            }

            doInTxn(new ConAction() {
              public void run(BlobStoreConnection con) throws Exception {
                for (int o = 0; o < numObjects; o++) {
                  int    idx = rng.nextInt(high - low) + low;
                  URI    id  = URI.create("urn:blobStressTest" + idx);
                  String val = "v" + idx;

                  Blob b = con.getBlob(id, null);
                  if (b.exists()) {
                    assertEquals(getBody(b), val);
                    found[0]++;
                  }
                }
              }
            }, true);
          }

          if (found[0] == 0)
            System.out.println("Warning: this reader found no blobs");
        }
      }, failed);
    }

    // wait for things to end
    for (int t = 0; t < writers.length; t++)
      writers[t].join();

    synchronized (testDone) {
      testDone[0] = true;
    }

    for (int t = 0; t < readers.length; t++)
      readers[t].join();

    long t2 = System.currentTimeMillis();

    // remove the fillers again
    for (int b = 0; b < numFillers / 1000; b++) {
      final int start = b * 1000;

      doInTxn(new ConAction() {
        public void run(BlobStoreConnection con) throws Exception {
          for (int idx = start; idx < start + 1000; idx++)
            con.getBlob(URI.create("urn:blobStressTestFiller" + idx), null).delete();
        }
      }, true);
    }

    long t3 = System.currentTimeMillis();

    System.out.println("Time to create " + numFillers + " fillers: " + ((t1 - t0) / 1000.) + " s");
    System.out.println("Time to remove " + numFillers + " fillers: " + ((t3 - t2) / 1000.) + " s");
    System.out.println("Time to run test (" + numWriters + "/" + numRounds + "/" + numObjects +
                       "): " + ((t2 - t1) / 1000.) + " s");

    assertFalse(failed[0]);
  }

  /**
   * Test that things get cleaned up. This runs after all other tests that create or otherwise
   * manipulate blobs.
   */
  @Override
  public void testCleanup() throws Exception {
    super.testCleanup();

    // verify that the tables are truly empty
    Connection connection = DriverManager.getConnection("jdbc:derby:" + dbDir);
    ResultSet rs =
        connection.createStatement().executeQuery("SELECT * FROM " + TransactionalStore.NAME_TABLE);
    assertFalse(rs.next(), "unexpected entries in name-map table;");

    rs = connection.createStatement().executeQuery("SELECT * FROM " + TransactionalStore.DEL_TABLE);
    assertFalse(rs.next(), "unexpected entries in deleted-list table;");
  }

  private void doInTxn(ConAction a, boolean commit) throws Exception {
    tm.begin();
    BlobStoreConnection con = store.openConnection(tm.getTransaction(), null);

    try {
      a.run(con);

      if (commit)
        tm.commit();
      else
        tm.rollback();
    } finally {
      if (tm.getTransaction() != null) {
        try {
          tm.rollback();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      con.close();
    }
  }
}
