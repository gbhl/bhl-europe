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

import java.net.URI;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.akubraproject.BlobStore;
import org.akubraproject.impl.AbstractBlobStore;

/**
 * An abstract base class for store implementations that multiplexes its backing stores to give
 * a unified view.
 *
 * <p>For any operation involving a blobId, this store selects a backing store as the target
 * for that operation. This selection is made based on a sub-class's implementation of {@link
 * AbstractMuxConnection#getStore}. It is expected that the store selection made by this method
 * will also take the store capability into consideration.</p>
 *
 * <p>Renames across BlobStores is implemented as a copy. Note that any error during that
 * process may leave partially completed blobs if the underlying blobs are non transactional.</p>
 *
 * <p>Iterations etc. are performed across all stores.</p>

 * <p><em>Note</em>: For multiplexing non-transactional stores along with transactional stores
 * (eg. accessing www resources along with other transactional stores), the non-transactional
 * store must be wrapped such that {@link #openConnection(javax.transaction.Transaction,
 * java.util.Map)} does not throw an exception. See {@link TransactionNeutralStoreWrapper} for use
 * in such cases.
 *
 * @author Pradeep Krishnan
 */
public abstract class AbstractMuxStore extends AbstractBlobStore {
  /**
   * The list of backing stores. Note that the order is same as the user supplied list.
   */
  protected List<BlobStore> backingStores = Collections.emptyList();

  /**
   * Creates a new AbstractMuxStore object.
   *
   * @param id an identifier for this store
   * @param stores the list of stores to multiplex
   */
  protected AbstractMuxStore(URI id, BlobStore ... stores) {
    super(id);
    setBackingStores(Arrays.asList(stores));
  }

  /**
   * Gets the list of backing stores.
   *
   * @return the stores that are being multiplexed
   */
  public List<?extends BlobStore> getBackingStores() {
    return backingStores;
  }

  /**
   * Sets the list of backing stores.
   *
   * @param stores the list of stores to be multiplexed
   */
  public void setBackingStores(List<?extends BlobStore> stores) {
    Set<URI> ids               = new HashSet<URI>();

    for (BlobStore s : stores)
      if (!ids.add(s.getId()))
        throw new IllegalArgumentException("Duplicate store-id: " + s.getId());

    backingStores = Collections.unmodifiableList(stores);
  }
}
