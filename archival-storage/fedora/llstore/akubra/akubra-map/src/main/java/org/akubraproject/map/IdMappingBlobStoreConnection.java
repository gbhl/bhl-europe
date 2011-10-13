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
import java.io.InputStream;

import java.net.URI;

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.impl.BlobStoreConnectionWrapper;

/**
 * Wraps the internal {@link BlobStoreConnection} to provide id mapping where
 * appropriate.
 *
 * @author Chris Wilper
 */
class IdMappingBlobStoreConnection extends BlobStoreConnectionWrapper {
  private final IdMapper mapper;

  /**
   * Creates an instance.
   *
   * @param store the store from which this connection originated.
   * @param connection the wrapped connection.
   * @param mapper the mapper to use.
   */
  public IdMappingBlobStoreConnection(BlobStore store,
                                      BlobStoreConnection connection,
                                      IdMapper mapper) {
    super(store, connection);
    this.mapper = mapper;
  }

  @Override
  public Blob getBlob(URI blobId, Map<String, String> hints)
      throws IOException, UnsupportedIdException, UnsupportedOperationException {
    Blob internalBlob;
    if (blobId == null)
      internalBlob = delegate.getBlob(null, hints);
    else
      internalBlob = delegate.getBlob(mapper.getInternalId(blobId), hints);
    return new IdMappingBlob(this, internalBlob, mapper);
  }

  @Override
  public Blob getBlob(InputStream content, long estimatedSize, Map<String, String> hints)
      throws IOException, UnsupportedOperationException {
    Blob internalBlob = delegate.getBlob(content, estimatedSize, hints);
    return new IdMappingBlob(this, internalBlob, mapper);
  }

  @Override
  public Iterator<URI> listBlobIds(final String filterPrefix) throws IOException {
    // list the appropriate internal ids
    String intPrefix = null;
    Iterator<URI> intIterator;
    if (filterPrefix == null) {
      intIterator = delegate.listBlobIds(null);
    } else {
      intPrefix = mapper.getInternalPrefix(filterPrefix);
      intIterator = delegate.listBlobIds(intPrefix);
    }

    // transform internal ids to external form on the way out
    Iterator<URI> extIterator = Iterators.transform(intIterator, new Function<URI, URI>() {
      public URI apply(URI uri) {
        return mapper.getExternalId(uri);
      }
    });

    if (filterPrefix != null && intPrefix == null) {
      // also need to post-filter
      return Iterators.filter(extIterator, new Predicate<URI>() {
        public boolean apply(URI uri) {
          return uri.toString().startsWith(filterPrefix);
        }
      });
    } else {
      // no need to filter
      return extIterator;
    }
  }
}
