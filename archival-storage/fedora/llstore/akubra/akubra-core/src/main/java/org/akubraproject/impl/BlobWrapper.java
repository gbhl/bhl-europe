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
import java.net.URI;
import java.util.Map;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;

/**
 * Simple wrapper implementation that delegates all calls to the wrapped blob.
 * Subclass and override to customize.
 *
 * @author Ronald Tschalär
 */
public class BlobWrapper extends AbstractBlob {
  /** The wrapped blob to which all calls are delegated. */
  protected final Blob delegate;

  /**
   * Create a new BlobWrapper.
   *
   * @param delegate the blob to delegate the calls to
   */
  public BlobWrapper(Blob delegate) {
    this(delegate, delegate.getConnection(), delegate.getId());
  }

  /**
   * Create a new BlobWrapper.
   *
   * @param delegate the blob to delegate the calls to
   * @param con the blob store connection. Usually different from the delegate's connection.
   */
  public BlobWrapper(Blob delegate, BlobStoreConnection con) {
    this(delegate, con, delegate.getId());
  }

  /**
   * Create a new BlobWrapper.
   *
   * @param delegate the blob to delegate the calls to
   * @param id the blob id. Could be different from the delegate's id.
   */
  public BlobWrapper(Blob delegate, URI id) {
    this(delegate, delegate.getConnection(), id);
  }

  /**
   * Create a new BlobWrapper.
   *
   * @param delegate the blob to delegate the calls to
   * @param con the blob store connection. Usually different from the delegate's connection.
   * @param id the blob id. Could be different from the delegate's id.
   */
  public BlobWrapper(Blob delegate, BlobStoreConnection con, URI id) {
    super(con, id);
    this.delegate = delegate;
  }

  @Override
  public URI getCanonicalId() throws IOException {
    return delegate.getCanonicalId();
  }

  @Override
  public InputStream openInputStream() throws IOException {
    ensureOpen();
    return delegate.openInputStream();
  }

  @Override
  public OutputStream openOutputStream(long estimatedSize, boolean overwrite) throws IOException {
    ensureOpen();
    return delegate.openOutputStream(estimatedSize, overwrite);
  }

  @Override
  public long getSize() throws IOException{
    ensureOpen();
    return delegate.getSize();
  }

  @Override
  public boolean exists() throws IOException {
    ensureOpen();
    return delegate.exists();
  }

  @Override
  public void delete() throws IOException {
    ensureOpen();
    delegate.delete();
  }

  @Override
  public Blob moveTo(URI blobId, Map<String, String> hints) throws IOException {
    ensureOpen();

    Blob nb = delegate.moveTo(blobId, hints);
    return getConnection().getBlob(nb.getId(), hints);
  }
}
