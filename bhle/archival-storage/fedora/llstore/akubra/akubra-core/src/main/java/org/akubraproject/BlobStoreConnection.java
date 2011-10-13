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
import java.io.InputStream;

import java.net.URI;

import java.util.Iterator;
import java.util.Map;

/**
 * Interface to abstract the idea of a connection to a transaction based blob store
 *
 * @author Chris Wilper
 * @author Pradeep Krishnan
 * @author Ronald Tschalär
 */
public interface BlobStoreConnection {
  /**
   * Gets the blob store associated with this connection.
   *
   * @return the blob store.
   */
  BlobStore getBlobStore();

  /**
   * Gets the blob with the given id.
   *
   * @param blobId the blob id; may be null if the store supports id-generation
   * @param hints A set of hints to allow the implementation to optimize the operation (can be
   *              null)
   *
   * @return the blob. Cannot be null and must have the passed in blobId if blobId is not null. If
   *         the passed in blobId is null, a new and unique (for the store) id must be generated if
   *         the store is capable of generating ids; the thus returned blob may exist, in which
   *         case it will have an empty content. In either case there is no requirement that the
   *         returned blob must {@link Blob#exists exist}. However there is a requirement that the
   *         {@link Blob#getConnection getConnection()} method of the returned Blob must return
   *         this connection object.
   *
   * @throws IOException for IO errors
   * @throws UnsupportedIdException if blobId is not in a recognized/usable pattern by this store
   * @throws UnsupportedOperationException if <var>blobId</var> is null and this store is not
   *                                       capable of generating ids.
   */
  Blob getBlob(URI blobId, Map<String, String> hints)
        throws IOException, UnsupportedIdException, UnsupportedOperationException;

  /**
   * Creates a blob with the given content. For Content Addressible Storage (CAS) systems,
   * this is the only way to create a Blob; for all other stores there is also {@link #getBlob(URI,
   * Map) getBlob}.{@link Blob#openOutputStream openOutputStream}.
   *
   * @param content the contents of the blob
   * @param estimatedSize the estimated size of the data if known (or -1 if unknown).
   *                      This can allow for the implementation to make better decisions
   *                      on buffering or reserving space.
   * @param hints A set of hints to allow the implementation to optimize the operation (can be
   *              null)
   *
   * @return the blob. Cannot be null and must have a generated id. The {@link Blob#getConnection
   *                   getConnection()} method must return this connection object.
   *
   * @throws IOException for IO errors
   * @throws UnsupportedOperationException if this store cannot generate new id and create a new
   *                                       blob
   */
  Blob getBlob(InputStream content, long estimatedSize, Map<String, String> hints)
        throws IOException, UnsupportedOperationException;

  /**
   * Gets an iterator over the ids of all blobs in this store.
   *
   * @param filterPrefix If not null, the list will be limited to those blob-ids beginning with
   *                     this prefix.
   *
   * @return The iterator over the blob-ids.
   * @throws IOException if an error occurred getting the list of blob ids
   */
  Iterator<URI> listBlobIds(String filterPrefix) throws IOException;

  /**
   * Flush all blobs associated with this connection and fsync. After this method completes all
   * data should be comitted to stable storage (for stores that are backed by stable storage).
   * However, whether this is really the case will depend on the storage used by the store (e.g.
   * for a local filesystem based store it'll depend on the OS config and hardware, i.e. whether
   * the disk's write cache is flushed or not).
   *
   * <p>This may or may not flush any open output-streams. For reliable results ensure that all
   * output streams are closed before calling sync.
   *
   * @throws IOException if any error occurred trying to flush and sync
   * @throws UnsupportedOperationException if the store is not capable of syncing
   */
  void sync() throws IOException, UnsupportedOperationException;

  /**
   * Close the connection to the blob store. After this, all Blob and BlobStoreConnection
   * operations except for {@link #getBlobStore getBlobStore}, {@link #isClosed isClosed},
   * {@link #close close}, {@link Blob#getId Blob.getId}, and {@link Blob#getConnection
   * Blob.getConnection} will throw an {@link java.lang.IllegalStateException
   * IllegalStateException}.
   *
   * <p>This method is idempotent and may be called multiple times.
   */
  void close();

  /**
   * Tests if the connection to the blob store is closed.
   *
   * @return true if the connection is closed.
   * @see #close
   */
  boolean isClosed();
}
