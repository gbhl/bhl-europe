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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.akubraproject.Blob;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.akubraproject.impl.BlobWrapper;

/**
 * A wrapped blob for use by implementations of {@link AbstractMuxConnection}. This ensures
 * that {@link #getConnection()} returns the connection from the mux store layer rather than the
 * backing store. Additionally this supports {@link #moveTo(URI, Map)} across blob stores using {@link
 * #moveByCopy(Blob, URI, URI)}.
 *
 * @author Pradeep Krishnan
 */
public class MuxBlob extends BlobWrapper {
  private static final Logger log = LoggerFactory.getLogger(MuxBlob.class);

  /**
   * Creates a new MuxBlob instance.
   *
   * @param delegate the Blob instance returned by a backing store
   * @param con the mux store connection
   */
  public MuxBlob(Blob delegate, AbstractMuxConnection con) {
    super(delegate, con);
  }

  @Override
  public Blob moveTo(URI blobId, Map<String, String> hints) throws IOException {
    MuxBlob dest   = (MuxBlob) getConnection().getBlob(blobId, hints);
    URI thisStore  = delegate.getConnection().getBlobStore().getId();
    URI otherStore = dest.delegate.getConnection().getBlobStore().getId();

    if (!thisStore.equals(otherStore))
      moveByCopy(dest, thisStore, otherStore);
    else
      delegate.moveTo(blobId, hints);

    return dest;
  }

  /**
   * Performs a {@link #moveTo(URI, Map)} operation by copy since the blobs are in different stores.
   *
   * @param blob the destination
   * @param thisStore the store where this Blob exists
   * @param otherStore the store where the destination Blob should exist
   *
   * @throws IOException on an error in copy
   * @throws DuplicateBlobException if a blob with the same id as the destination blob exists in
   *         the destination blob store
   * @throws MissingBlobException if this blob does not exist
   */
  protected void moveByCopy(Blob blob, URI thisStore, URI otherStore)
                     throws IOException, DuplicateBlobException, MissingBlobException {
    log.warn("Performing moveTo() by copy for '" + getId() + "' to '" + blob.getId()
             + "' from store '" + thisStore + "' to store '" + otherStore + "'");

    InputStream  in      = openInputStream();
    OutputStream out     = null;
    boolean      created = false;

    try {
      out = blob.openOutputStream(getSize(), false);
      created = true;
      IOUtils.copy(in, out);
      out.close();
      out = null;
      in.close();
      in = null;

      delete();
      created = false;
    } finally {
      if (in != null)
        IOUtils.closeQuietly(in);

      if (out != null)
        IOUtils.closeQuietly(out);

      try {
        if (created)
          blob.delete();
      } catch (Exception de) {
        log.warn("Ignored deletion failure for " + blob.getId());
      }
    }
  }
}
