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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.akubraproject.impl.AbstractBlob;
import org.akubraproject.impl.StreamManager;

/**
 * Simple in-memory blob store.
 *
 * @author Ronald Tschalär
 */
class MemBlob extends AbstractBlob {
  private final Map<URI, MemData> blobs;
  private final StreamManager     streamMgr;

  /**
   * Create a new in-memory blob.
   *
   * @param id        the blob's id
   * @param blobs     the blob-map to use (shared, hence needs to be synchronized)
   * @param streamMgr the stream-manager to use
   * @param owner     the blob-store connection we belong to
   */
  MemBlob(URI id, Map<URI, MemData> blobs, StreamManager streamMgr, BlobStoreConnection owner) {
    super(owner, id);
    this.blobs     = blobs;
    this.streamMgr = streamMgr;
  }

  @Override
  public URI getCanonicalId() {
    return getId();
  }

  @Override
  public boolean exists() throws IOException {
    ensureOpen();
    synchronized (blobs) {
      return blobs.containsKey(id);
    }
  }

  @Override
  public void delete() throws IOException {
    ensureOpen();

    synchronized (blobs) {
      blobs.remove(id);
    }
  }

  @Override
  public Blob moveTo(URI blobId, Map<String, String> hints)
        throws IOException, MissingBlobException, NullPointerException, IllegalArgumentException,
               DuplicateBlobException {
    ensureOpen();

    MemBlob dest = (MemBlob)getConnection().getBlob(blobId, hints);

    synchronized (blobs) {
      if (dest.exists())
        throw new DuplicateBlobException(blobId, "Destination blob already exists");

      MemData data = blobs.remove(id);
      if (data == null)
        throw new MissingBlobException(getId());

      blobs.put(dest.getId(), data);
    }

    return dest;
  }

  @Override
  public InputStream openInputStream() throws IOException {
    ensureOpen();
    return streamMgr.manageInputStream(getConnection(), getData().getInputStream());
  }

  @Override
  public OutputStream openOutputStream(long estimatedSize, boolean overwrite) throws IOException {
    ensureOpen();

    MemData data;

    synchronized (blobs) {
      data = blobs.get(id);
      if (!overwrite && data != null)
        throw new DuplicateBlobException(getId(), "Blob already exists");

      if (data == null || estimatedSize > data.bufferSize()) {
        data = new MemData(Math.max((int) Math.min(estimatedSize, Integer.MAX_VALUE), 1024));
        blobs.put(id, data);
      } else {
        data.reset();
      }
    }

    return streamMgr.manageOutputStream(getConnection(), data);
  }

  @Override
  public long getSize() throws IOException {
    ensureOpen();
    return getData().size();
  }

  private MemData getData() throws MissingBlobException {
    synchronized (blobs) {
      MemData data = blobs.get(id);
      if (data == null)
        throw new MissingBlobException(getId());
      return data;
    }
  }
}
