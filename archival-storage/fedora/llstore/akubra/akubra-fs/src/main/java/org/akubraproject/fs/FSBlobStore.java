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
package org.akubraproject.fs;

import java.io.File;

import java.net.URI;

import java.util.Map;

import javax.transaction.Transaction;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.impl.AbstractBlobStore;
import org.akubraproject.impl.StreamManager;

/**
 * Filesystem-backed BlobStore implementation.
 * <p>
 * This is a <em>non-transactional</em> store that provides read/write access to
 * blobs in a given base directory on the local filesystem.
 * <p>
 * <h2>Blob Ids</h2>
 * Blob ids are <code>file:</code> URIs that specify a file path relative to the
 * store's base directory. Paths may include ".." segments, but cannot
 * specify files outside the store's base directory. For example:
 * <ul>
 *   <li> Good: <code>file:path/to/file.txt</code>
 *   <li> Good: <code>file:path/../file.txt</code>
 *   <li> Bad: <code>file:///absolute/path/to/file.txt</code>
 *   <li> Bad: <code>file:path/../../file-outside-base.txt</code>
 *   <li> Bad: <code>file:path/to/dir/</code>
 * </ul>
 * <p>
 * <h2>Id Generation</h2>
 * This store does not support id generation.
 * <p>
 * <h2>Canonical Ids</h2>
 * All {@link Blob}s provided by this store will return a non-null value from
 * {@link Blob#getCanonicalId}. The canonical id is the normalized form of the
 * original id, according to the following rules:
 * <ul>
 *   <li> ".." segments are resolved.</li>
 *   <li> "." segments are removed.</li>
 *   <li> adjacent "/" characters replaced with a single "/".</li>
 *   <li> scheme normalized to lowercase</li>
 * </ul>
 *
 * @author Chris Wilper
 */
public class FSBlobStore extends AbstractBlobStore {
  /**
   * Connection hint indicating that the client will not call {@link BlobStoreConnection#sync}; the
   * associated value must be "true" (case insesitive).
   */
  public static final String WILL_NOT_SYNC = "org.akubraproject.will_not_sync";

  private final File baseDir;
  private final StreamManager manager = new StreamManager();

  /**
   * Creates an instance with the given id and base storage directory.
   *
   * @param id the unique identifier of this blobstore.
   * @param baseDir the base storage directory.
   */
  public FSBlobStore(URI id, File baseDir) {
    super(id);
    this.baseDir = baseDir;
  }

  @Override
  public BlobStoreConnection openConnection(Transaction tx, Map<String, String> hints) {
    if (tx != null) {
      throw new UnsupportedOperationException();
    }

    boolean no_sync = (hints != null) && Boolean.parseBoolean(hints.get(WILL_NOT_SYNC));
    return new FSBlobStoreConnection(this, baseDir, manager, no_sync);
  }
}
