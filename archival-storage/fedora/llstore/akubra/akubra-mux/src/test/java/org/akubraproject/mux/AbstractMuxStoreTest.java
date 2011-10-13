/* $HeadURL::                                                                            $
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
package org.akubraproject.mux;

import java.io.IOException;

import java.net.URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.transaction.Transaction;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.impl.AbstractBlobStore;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Tests for AbstractMuxStore.
 *
 * @author Pradeep Krishnan
 */
public class AbstractMuxStoreTest {
  private final URI        storeId = URI.create("urn:test:mux:store");
  private AbstractMuxStore store;

  /**
   * Sets up things for all stores.
   */
  @BeforeSuite
  public void setUp() {
    store =
      new AbstractMuxStore(storeId) {
          @Override
          public BlobStoreConnection openConnection(Transaction tx, Map<String, String> hints)
                                             throws UnsupportedOperationException, IOException {
            return new AbstractMuxConnection(this, tx) {
                @Override
                public BlobStore getStore(URI blobId, Map<String, String> hints)
                                   throws IOException, UnsupportedIdException {
                  throw new UnsupportedOperationException("Not needed for this test.");
                }
              };
          }
        };
  }

  @AfterSuite
  public void tearDown() {
  }

  /**
   * Tests the constructor.
   */
  @Test
  public void testAbstractMuxStore() {
    assertEquals(storeId, store.getId());
  }

  /**
   * Tests the backing store management.
   */
  @Test
  public void testBackingStores() {
    store.setBackingStores(new ArrayList<BlobStore>());
    assertTrue(store.getBackingStores().isEmpty());

    BlobStore store1               = createMockStore(URI.create("urn:store:1"));
    store.setBackingStores(Collections.singletonList(store1));
    assertEquals(1, store.getBackingStores().size());
    assertEquals(store1, store.getBackingStores().get(0));

    BlobStore store2 = createMockStore(URI.create("urn:store:2"));
    BlobStore store3 = createMockStore(URI.create("urn:store:3"));

    store.setBackingStores(Arrays.asList(store1, store2, store3));
    assertEquals(3, store.getBackingStores().size());
    assertEquals(store1, store.getBackingStores().get(0));
    assertEquals(store2, store.getBackingStores().get(1));
    assertEquals(store3, store.getBackingStores().get(2));

    try {
      store.setBackingStores(Arrays.asList(store1, store2, store3, store1));
      fail("Failed to rcv expected exception.");
    } catch (IllegalArgumentException e) {
    }
  }

  private MockBlobStore createMockStore(URI id) {
    return new MockBlobStore(id);
  }

  private static final class MockBlobStore extends AbstractBlobStore {
    private MockBlobStore(URI id) {
      super(id);
    }

    @Override
    public BlobStoreConnection openConnection(Transaction tx, Map<String, String> hints)
                                       throws UnsupportedOperationException, IOException {
      return null;
    }
  }
}
