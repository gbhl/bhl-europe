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
import java.net.URI;
import java.util.Iterator;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;

/**
 * Simple wrapper implementation that delegates {@link #listBlobIds listBlobIds} and {@link #sync
 * sync } calls to the wrapped blob-store-connection, and implements {@link #close close} to close
 * both this connection and the underlying connection. Subclasses must at least implement {@link
 * #getBlob(URI, java.util.Map) getBlob}.
 *
 * @author Ronald Tschalär
 */
public abstract class BlobStoreConnectionWrapper extends AbstractBlobStoreConnection {
  /** The wrapped blob-store-connection to which all calls are delegated. */
  protected final BlobStoreConnection delegate;

  /**
   * Create a new BlobStoreConnectionWrapper.
   *
   * @param owner    the blob-store this connection belongs to
   * @param delegate the blob-store-connection to delegate the calls to
   */
  protected BlobStoreConnectionWrapper(BlobStore owner, BlobStoreConnection delegate) {
    this(owner, delegate, null);
  }

  /**
   * Create a new BlobStoreConnectionWrapper.
   *
   * @param owner         the blob-store this connection belongs to
   * @param delegate      the blob-store-connection to delegate the calls to
   * @param streamManager the stream-manager to use, if any
   */
  protected BlobStoreConnectionWrapper(BlobStore owner, BlobStoreConnection delegate,
                                       StreamManager streamManager) {
    super(owner, streamManager);
    this.delegate = delegate;
  }

  @Override
  public Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
    return delegate.listBlobIds(filterPrefix);
  }

  @Override
  public void sync() throws IOException, UnsupportedOperationException {
    delegate.sync();
  }

  @Override
  public void close() {
    try {
      super.close();
    } finally {
      delegate.close();
    }
  }
}
