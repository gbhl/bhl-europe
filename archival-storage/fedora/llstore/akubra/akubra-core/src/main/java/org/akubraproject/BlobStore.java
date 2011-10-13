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
package org.akubraproject;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.transaction.Transaction;

/**
 * Interface to abstract the idea of a general transaction based blob store
 *
 * @author Chris Wilper
 * @author Pradeep Krishnan
 * @author Ronald Tschalär
 */
public interface BlobStore {
  /**
   * Return the identifier associated with the store
   *
   * @return the URI identifying the blob store
   */
  URI getId();

  /**
   * Opens a connection to the blob store.
   * <p>
   * If the blob store is transactional, the caller must provide a
   * <code>Transaction</code>.  Once provided, the connection will be valid for
   * the life of the transaction, at the end of which it will be automatically
   * {@link BlobStoreConnection#close close()}'d.
   * <p>
   * If the blob store is not transactional, the caller must provide
   * <code>null</code>.  Once provided, the connection will be valid until
   * explicitly closed.
   * <p>
   * Implementations are expected to do any desired pooling of underlying
   * connections themselves.
   * <p>
   * @param tx the transaction associated with this connection, or null if
   *     the blob store is not transactional
   * @param hints A set of hints to allow the implementation to optimize the
   *              operation (can be null)
   * @return the connection to the blob store
   * @throws UnsupportedOperationException if the blob store is transactional
   *     but a Transaction is not provided by the caller, or if the blob store
   *     is not transactional and a Transaction is provided by the caller.
   * @throws IOException if an error occurred trying to open the connection.
   */
  BlobStoreConnection openConnection(Transaction tx, Map<String, String> hints)
      throws UnsupportedOperationException, IOException;
}
