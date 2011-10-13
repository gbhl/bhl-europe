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

package org.akubraproject.mux;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.transaction.Transaction;

import org.apache.commons.io.FileUtils;

import org.testng.annotations.Factory;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.mem.MemBlobStore;
import org.akubraproject.tck.TCKTestSuite;
import org.akubraproject.txn.derby.TransactionalStore;

/**
 * TCK test suite for multiplexing store.
 *
 * @author Ronald Tschalär
 */
public class MuxStoreTCKTest {
  @Factory
  public Object[] createTests() throws Exception {
    URI storeId1 = URI.create("urn:mux-tck-test:42");
    URI storeId2 = URI.create("urn:mux-tck-test:43");

    List<? extends BlobStore> nonTxnStores = Arrays.asList(
            new MemBlobStore(URI.create("urn:store:1")),
            new MemBlobStore(URI.create("urn:store:2"))
    );
    List<? extends BlobStore> txnStores = Arrays.asList(
            createTxnStore("mux-txn-text-1", new MemBlobStore(URI.create("urn:store:5"))),
            createTxnStore("mux-txn-text-2", new MemBlobStore(URI.create("urn:store:6")))
    );

    return new Object[] {
      new MuxStoreTestSuite(createMuxStore(storeId1, nonTxnStores), storeId1, false, true),
      new MuxStoreTestSuite(createMuxStore(storeId2, txnStores), storeId2, true, false),
    };
  }

  private BlobStore createMuxStore(URI storeId, List<? extends BlobStore> stores) {
    AbstractMuxStore store =
      new AbstractMuxStore(storeId) {
          @Override
          public BlobStoreConnection openConnection(Transaction tx, Map<String, String> hints)
                                             throws UnsupportedOperationException, IOException {
            return new TestConnection(this, tx);
          }
        };
    store.setBackingStores(stores);

    return store;
  }

  private BlobStore createTxnStore(String name, BlobStore backingStore) throws IOException {
    File base  = new File(System.getProperty("basedir"), "target");
    File dbDir = new File(base, name);
    FileUtils.deleteDirectory(dbDir);
    dbDir.getParentFile().mkdirs();

    System.setProperty("derby.stream.error.file", new File(base, "derby.log").toString());

    BlobStore store = new TransactionalStore(URI.create("urn:" + name), backingStore, dbDir.getPath());
    return store;
  }

  /** Our TCK test suite for the mux store */
  private static class MuxStoreTestSuite extends TCKTestSuite {
    public MuxStoreTestSuite(BlobStore store, URI storeId, boolean isTransactional,
                             boolean supportsIdGen) {
      super(store, storeId, isTransactional, supportsIdGen);
    }

    @Override
    protected URI getInvalidId() {
      return null;      // all ids are valid
    }

    /** all URI's are distinct */
    @Override
    protected URI[] getAliases(URI uri) {
      return new URI[] { uri };
    }

    @Override
    public void testOpenConnectionWithTransaction() {
      // backend connections are opened lazily
    }

    @Override
    public void testOpenConnectionNoTransaction() {
      // backend connections are opened lazily
    }
  }

  /** Simple mux-connection that round-robins between the stores */
  private class TestConnection extends AbstractMuxConnection {
    private int cntr = 0;

    private TestConnection(BlobStore store, Transaction txn) {
      super(store, txn);
    }

    @Override
    public BlobStore getStore(URI blobId, Map<String, String> hints)
                       throws IOException, UnsupportedIdException {
      // see if this blob belongs to a specific store
      if (blobId != null) {
        for (BlobStore s : ((AbstractMuxStore) getBlobStore()).getBackingStores())
          if (getConnection(s, null).listBlobIds(blobId.toString()).hasNext()
               && (getConnection(s, null).getBlob(blobId, hints) != null))
            return s;
      }

      // nope, so pick the next store in a round-robin fashion
      int numStores = ((AbstractMuxStore) getBlobStore()).getBackingStores().size();
      return ((AbstractMuxStore) getBlobStore()).getBackingStores().get(cntr++ % numStores);
    }
  }
}
