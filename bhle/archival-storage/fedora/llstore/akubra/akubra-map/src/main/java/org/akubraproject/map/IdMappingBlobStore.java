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
package org.akubraproject.map;

import java.io.IOException;

import java.net.URI;

import java.util.Map;

import javax.transaction.Transaction;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.impl.AbstractBlobStore;

/**
 * Wraps an existing {@link BlobStore} to provide a blob id mapping layer.
 *
 * @author Chris Wilper
 */
public class IdMappingBlobStore extends AbstractBlobStore {
  private final BlobStore store;
  private final IdMapper mapper;

  /**
   * Creates an instance.
   *
   * @param id the id associated with this store.
   * @param store the store to wrap.
   * @param mapper the mapper to use.
   */
  public IdMappingBlobStore(URI id, BlobStore store, IdMapper mapper) {
    super(id);
    this.store = store;
    this.mapper = mapper;
  }

  @Override
  public BlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) throws IOException {
    BlobStoreConnection connection = store.openConnection(tx, hints);
    return new IdMappingBlobStoreConnection(this, connection, mapper);
  }
}
