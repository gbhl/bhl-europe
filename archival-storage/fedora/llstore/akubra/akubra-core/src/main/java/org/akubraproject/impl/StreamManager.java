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
package org.akubraproject.impl;

import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;

import org.akubraproject.BlobStoreConnection;

/**
 * Utility class that tracks the open streams of a <code>BlobStore</code>. This also provides for
 * ensuring that streams that belong to a <code>BlobStoreConnection</code> are closed when the
 * connection is closed.
 *
 * @author Chris Wilper
 */
public class StreamManager {
  private static final Logger log = LoggerFactory.getLogger(StreamManager.class);

  /** Listens to close events. */
  protected final CloseListener listener;

  /** The set of open <code>OutputStream</code>s managed by this instance. */
  protected final Set<ManagedOutputStream> openOutputStreams
      = Collections.synchronizedSet(new HashSet<ManagedOutputStream>());

  /** The set of open <code>InputStream</code>s managed by this instance. */
  protected final Set<ManagedInputStream> openInputStreams
      = Collections.synchronizedSet(new HashSet<ManagedInputStream>());

  /**
   * Creates an instance.
   */
  public StreamManager() {
    listener = new CloseListener() {
      public void notifyClosed(Closeable closeable) {
        if (closeable instanceof InputStream) {
          synchronized (openInputStreams) {
            openInputStreams.remove(closeable);
            openInputStreams.notifyAll();
          }
        } else {
          synchronized (openOutputStreams) {
            openOutputStreams.remove(closeable);
            openOutputStreams.notifyAll();
          }
        }
      }
    };
  }

  /**
   * Provides a tracked wrapper around a given OutputStream.
   *
   * @param con the connection that the returned output-stream belongs to.
   * @param stream the stream to wrap.
   * @return the wrapped version of the stream.
   * @throws IOException if interrupted while trying to acquire the state-lock
   */
  public OutputStream manageOutputStream(BlobStoreConnection con, OutputStream stream)
      throws IOException {
    ManagedOutputStream managed = new ManagedOutputStream(listener, stream, con);
    openOutputStreams.add(managed);
    return managed;
  }

  /**
   * Provides a tracked wrapper around a given InputStream.
   *
   * @param con the connection that the returned input-stream belongs to.
   * @param stream the stream to wrap.
   * @return the wrapped version of the stream.
   * @throws IOException if interrupted while trying to acquire the state-lock
   */
  public InputStream manageInputStream(BlobStoreConnection con, InputStream stream)
      throws IOException {
    ManagedInputStream managed = new ManagedInputStream(listener, stream, con);
    openInputStreams.add(managed);
    return managed;
  }

  /**
   * Notification that a connection is closed. All its open streams are closed.
   *
   * @param con the connection that is closed
   */
  public void connectionClosed(BlobStoreConnection con) {
    Set<Closeable> closeables = new HashSet<Closeable>();
    synchronized (openOutputStreams) {
      for (ManagedOutputStream c : openOutputStreams)
        if (c.getConnection().equals(con))
          closeables.add(c);
    }

    synchronized (openInputStreams) {
      for (ManagedInputStream c : openInputStreams)
        if (c.getConnection().equals(con))
          closeables.add(c);
    }

    if (!closeables.isEmpty()) {
      log.warn("Auto-closing " + closeables.size() + " open streams for closed connection " + con);
      for (Closeable c : closeables) {
        if (c instanceof InputStream)
          IOUtils.closeQuietly((InputStream) c);
        else
          IOUtils.closeQuietly((OutputStream) c);
      }
    }
  }

  // how many output streams are open?
  int getOpenOutputStreamCount() {
    return openOutputStreams.size();
  }

  // how many input streams are open?
  int getOpenInputStreamCount() {
    return openInputStreams.size();
  }
}
