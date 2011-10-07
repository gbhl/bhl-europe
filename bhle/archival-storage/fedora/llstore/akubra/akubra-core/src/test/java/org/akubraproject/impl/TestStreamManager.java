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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;

/**
 * Unit Tests for {@link StreamManager}.
 *
 * @author Chris Wilper
 */
public class TestStreamManager {

  private final StreamManager manager = new StreamManager();

  /**
   * Manager should start with no tracked streams.
   */
  @Test(groups = { "init" })
  public void testInitialState() {
    assertEquals(manager.getOpenOutputStreamCount(), 0);
    assertEquals(manager.getOpenInputStreamCount(), 0);
  }

  /**
   * Managed OutputStreams should be tracked when open and forgotten when closed.
   */
  @Test(dependsOnGroups = { "init" })
  public void testManageOutputStream() throws Exception {
    OutputStream managed = manager.manageOutputStream(null, new ByteArrayOutputStream());
    assertEquals(manager.getOpenOutputStreamCount(), 1);
    managed.close();
    assertEquals(manager.getOpenOutputStreamCount(), 0);
  }

  /**
   * Managed InputStreams should be tracked when open and forgotten when closed.
   */
  @Test(dependsOnGroups = { "init" })
  public void testManageInputStream() throws Exception {
    InputStream managed = manager.manageInputStream(null, new ByteArrayInputStream(new byte[0]));
    assertEquals(manager.getOpenInputStreamCount(), 1);
    managed.close();
    assertEquals(manager.getOpenInputStreamCount(), 0);
  }

  /**
   * Managed Streams should be tracked when open and closed when connection is closed.
   */
  @Test(dependsOnGroups = { "init" })
  public void testTrackedConnectionCloses() throws Exception {
    BlobStoreConnection con1 = new MockConnection(manager);
    BlobStoreConnection con2 = new MockConnection(manager);

    manager.manageInputStream(con1, new ByteArrayInputStream(new byte[0]));
    manager.manageOutputStream(con1, new ByteArrayOutputStream());
    assertEquals(manager.getOpenInputStreamCount(), 1);
    assertEquals(manager.getOpenOutputStreamCount(), 1);

    manager.manageInputStream(con2, new ByteArrayInputStream(new byte[0]));
    manager.manageOutputStream(con2, new ByteArrayOutputStream());
    assertEquals(manager.getOpenInputStreamCount(), 2);
    assertEquals(manager.getOpenOutputStreamCount(), 2);

    con1.close();
    assertEquals(manager.getOpenInputStreamCount(), 1);
    assertEquals(manager.getOpenOutputStreamCount(), 1);

    con2.close();
    assertEquals(manager.getOpenInputStreamCount(), 0);
    assertEquals(manager.getOpenOutputStreamCount(), 0);
  }

  private static class MockConnection extends AbstractBlobStoreConnection {
    public MockConnection(StreamManager manager) {
      super(null, manager);
    }

    @Override
    public Blob getBlob(URI blobId, Map<String, String> hints) throws IOException {
      return null;
    }

    @Override
    public Iterator<URI> listBlobIds(String filterPrefix) throws IOException {
      return null;
    }

    @Override
    public void sync() throws IOException {
    }
  }
}
