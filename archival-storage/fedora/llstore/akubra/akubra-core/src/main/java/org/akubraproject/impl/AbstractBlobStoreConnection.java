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
package org.akubraproject.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract base class for blob store connections.
 *
 * @author Pradeep Krishnan
 */
public abstract class AbstractBlobStoreConnection implements BlobStoreConnection {
  private static final Logger log = LoggerFactory.getLogger(AbstractBlobStoreConnection.class);

  protected final BlobStore owner;
  protected final StreamManager streamManager;
  protected boolean closed = false;

  protected AbstractBlobStoreConnection(BlobStore owner) {
    this(owner, null);
  }

  protected AbstractBlobStoreConnection(BlobStore owner, StreamManager streamManager) {
    this.owner = owner;
    this.streamManager = streamManager;
  }

  @Override
  public BlobStore getBlobStore() {
    return owner;
  }

  @Override
  public Blob getBlob(InputStream content, long estimatedSize, Map<String, String> hints)
            throws IOException, UnsupportedOperationException {
    Blob blob = getBlob(null, hints);

    boolean success = false;
    try {
      OutputStream out = blob.openOutputStream(estimatedSize, true);
      try {
        IOUtils.copyLarge(content, out);
        out.close();
        out = null;
      } finally {
        if (out != null)
          IOUtils.closeQuietly(out);
      }

      success = true;
    } finally {
      if (!success) {
        try {
          blob.delete();
        } catch (Throwable t) {
          log.error("Error deleting blob after blob-creation failure", t);
        }
      }
    }

    return blob;
  }

  @Override
  public void close() {
    if (!closed) {
      closed = true;
      if (streamManager != null)
        streamManager.connectionClosed(this);
    }
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  /**
   * Helper that checks whether the connection is open and throws an exception if not.
   *
   * @throws IllegalStateException if the connection has been closed
   */
  protected void ensureOpen() throws IllegalStateException {
    if (isClosed())
      throw new IllegalStateException("Connection closed.");
  }
}
