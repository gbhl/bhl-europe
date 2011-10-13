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
import java.io.OutputStream;

import java.net.URI;
import java.util.Map;

/**
 * Interface to abstract the idea of a blob in the blob store.
 *
 * @author Chris Wilper
 * @author Pradeep Krishnan
 * @author Ronald Tschalär
 */
public interface Blob {
  /**
   * Gets the connection that provided this blob.
   *
   * @return the blob store connection that created this blob
   */
  BlobStoreConnection getConnection();

  /**
   * Gets the id of the blob. Blob ids are URIs as defined by RFC 3986, and therefore will
   * always have a scheme.
   *
   * @return the non-null immutable blob id
   */
  URI getId();

  /**
   * Gets the canonical id of the blob, if known. The definition of what is canonical is
   * up to the individual store implementation. When available, the canonical id should be
   * used in caches and other indexes where duplication is undesirable.
   *
   * @return the canonical id, if known; null otherwise.
   * @throws IOException if an error occurred
   */
  URI getCanonicalId() throws IOException;

  /**
   * Opens a new InputStream for reading the content.
   *
   * @return the input stream.
   * @throws MissingBlobException if the blob does not {@link #exists exist}.
   * @throws IOException if the stream cannot be opened for any other reason.
   */
  InputStream openInputStream() throws IOException, MissingBlobException;

  /**
   * Opens a new OutputStream for writing the content. If the blob does not exist it is created
   * with the data written to the stream as the content; else if <var>overwrite</var> is true the
   * existing content is replaced with the new data.
   *
   * @param estimatedSize the estimated size of the data if known (or -1 if unknown).
   *                      This can allow for the implementation to make better decisions
   *                      on buffering or reserving space.
   * @param overwrite     if false and this blob already exists then do not overwrite the contents
   *                      but throw an exception instead
   * @return the output stream.
   * @throws DuplicateBlobException if this blob exists and <var>overwrite</var> is false
   * @throws IOException if the stream cannot be opened for any other reason.
   */
  OutputStream openOutputStream(long estimatedSize, boolean overwrite)
      throws IOException, DuplicateBlobException;

  /**
   * Gets the size of the blob, in bytes.
   *
   * @return the size in bytes, or -1 if unknown
   * @throws MissingBlobException if the blob does not {@link #exists exist}.
   * @throws IOException if an error occurred during
   */
  long getSize() throws IOException, MissingBlobException;

  /**
   * Tests if a blob with this id exists in this blob-store.
   *
   * @return true if the blob denoted by this id exists; false otherwise.
   *
   * @throws IOException if an error occurred during existence check
   * @see #openOutputStream
   * @see #delete
   */
  boolean exists() throws IOException;

  /**
   * Removes this blob from the store. This operation is idempotent and does
   * not throw an exception if the blob does not {@link #exists exist}.
   *
   * @throws IOException if the blob cannot be deleted for any reason
   */
  void delete() throws IOException;

  /**
   * Move this blob under the new id. Before the move, this blob must exist and the destination
   * blob must not. After the move, this blob will not exist but the destination blob will.
   *
   * @param blobId the blob id; may be null if the store supports id-generation
   * @param hints A set of hints to allow the implementation to optimize the operation (can be
   *              null)
   *
   * @return the resulting Blob from the move
   *
   * @throws UnsupportedIdException if blobId is not in a recognized/usable pattern by this store
   * @throws UnsupportedOperationException if <var>blobId</var> is null and this store is not
   *                                       capable of generating ids.
   * @throws MissingBlobException if this blob does not exist
   * @throws DuplicateBlobException if a blob with <var>blobId</var> already exists
   * @throws IOException if an error occurs while attempting the operation
   */
  Blob moveTo(URI blobId, Map<String, String> hints)
      throws DuplicateBlobException, IOException, MissingBlobException, NullPointerException,
             IllegalArgumentException;
}
