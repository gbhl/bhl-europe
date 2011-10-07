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

package org.akubraproject.mem;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import org.akubraproject.Blob;
import org.akubraproject.impl.AbstractBlobStoreConnection;
import org.akubraproject.impl.StreamManager;

/**
 * Connection implementation for in-memory blob store.
 *
 * @author Ronald Tschalär
 */
class MemConnection extends AbstractBlobStoreConnection {
  private final Map<URI, MemData> blobs;

  /**
   * Create a new connection.
   *
   * @param owner     the owning blob-store
   * @param blobs     the blob-map to use (shared, hence needs to be synchronized)
   * @param streamMgr the stream-manager to use
   */
  MemConnection(MemBlobStore owner, Map<URI, MemData> blobs, StreamManager streamMgr) {
    super(owner, streamMgr);
    this.blobs     = blobs;
  }

  @Override
  public Blob getBlob(URI blobId, Map<String, String> hints) {
    ensureOpen();

    if (blobId == null) {
      synchronized (blobs) {
        do {
          blobId = MemBlobStore.getRandomId("urn:mem-store:gen-id:");
        } while (blobs.containsKey(blobId));
      }
    }

    return new MemBlob(blobId, blobs, streamManager, this);
  }

  @Override
  public Iterator<URI> listBlobIds(final String filterPrefix) {
    ensureOpen();

    synchronized (blobs) {
      return Iterators.filter(new ArrayList<URI>(blobs.keySet()).iterator(), new Predicate<URI>() {
         public boolean apply(URI uri) {
           return ((filterPrefix == null) || uri.toString().startsWith(filterPrefix));
         }
      });
    }
  }

  @Override
  public void sync() {
    ensureOpen();
  }
}
