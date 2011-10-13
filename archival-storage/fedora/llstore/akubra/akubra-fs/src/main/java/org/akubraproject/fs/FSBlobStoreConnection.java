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
import java.io.FileInputStream;
import java.io.IOException;

import java.net.URI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.impl.AbstractBlobStoreConnection;
import org.akubraproject.impl.StreamManager;

/**
 * Filesystem-backed BlobStoreConnection implementation.
 *
 * @author Chris Wilper
 */
class FSBlobStoreConnection extends AbstractBlobStoreConnection {
  private static final Logger log = LoggerFactory.getLogger(FSBlobStoreConnection.class);

  private final File baseDir;
  private final Set<File>     modified;

  FSBlobStoreConnection(BlobStore blobStore, File baseDir, StreamManager manager, boolean noSync) {
    super(blobStore, manager);
    this.baseDir = baseDir;
    this.modified = noSync ? null : new HashSet<File>();
  }

  @Override
  public Blob getBlob(URI blobId, Map<String, String> hints) throws IOException {
    ensureOpen();

    if (blobId == null)
      throw new UnsupportedOperationException();

    return new FSBlob(this, baseDir, blobId, streamManager, modified);
  }

  @Override
  public Iterator<URI> listBlobIds(String filterPrefix) {
    ensureOpen();
    return new FSBlobIdIterator(baseDir, filterPrefix);
  }

  @Override
  public void sync() throws IOException {
    ensureOpen();

    if (modified == null)
      throw new UnsupportedOperationException("You promised you weren't going to call sync!");

    for (File f : modified) {
      try {
        FileInputStream fis = new FileInputStream(f);
        try {
          fis.getFD().sync();
        } finally {
          fis.close();
        }
      } catch (IOException ioe) {
        log.warn("Error sync'ing file '" + f + "'", ioe);
      }
    }

    modified.clear();
  }

  @Override
  public void close() {
    if (modified != null)
      modified.clear();

    super.close();
  }
}
