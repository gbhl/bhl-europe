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
package org.akubraproject.map;

import java.io.OutputStream;
import java.io.StringReader;

import java.net.URI;

import com.google.common.collect.Iterators;

import org.apache.commons.io.IOUtils;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.mem.MemBlobStore;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for {@link IdMappingBlobStoreConnection}.
 *
 * @author Chris Wilper
 */
public class IdMappingBlobStoreConnectionTest {

  private static final URI blobId1 = URI.create("urn:blob:1");
  private static final URI blobId2 = URI.create("urn:blob:2");
  private static final URI blobId3 = URI.create("info:blob:2");

  /**
   * Listing blobs with and without prefix filters should behave as expected
   * when the prefix is mappable.
   */
  @Test
  public void testListBlobsPrefixMappable() throws Exception {
    testListBlobs(getTestConnection(true));
  }

  /**
   * Listing blobs with and without prefix filters should behave as expected
   * when the prefix is NOT mappable.
   */
  @Test
  public void testListBlobsPrefixNotMappable() throws Exception {
    testListBlobs(getTestConnection(false));
  }

  private void testListBlobs(BlobStoreConnection c) throws Exception {
    addTestBlob(c, blobId1);
    addTestBlob(c, blobId2);
    addTestBlob(c, blobId3);
    assertEquals(Iterators.size(c.listBlobIds(null)), 3);
    assertEquals(Iterators.size(c.listBlobIds("urn:blob:1")), 1);
    assertEquals(Iterators.size(c.listBlobIds("urn")), 2);
    assertEquals(Iterators.size(c.listBlobIds("info")), 1);
  }

  private static BlobStoreConnection getTestConnection(boolean prefixMappable)
      throws Exception {
    BlobStore store = new IdMappingBlobStore(URI.create("urn:test-store"),
        new MemBlobStore(), new MockIdMapper("internal:", prefixMappable));
    return store.openConnection(null, null);
  }

  private static void addTestBlob(BlobStoreConnection connection,
                                  URI blobId) throws Exception {
    Blob blob = connection.getBlob(blobId, null);
    OutputStream out = blob.openOutputStream(-1, false);
    IOUtils.copy(new StringReader(blobId.toString()), out);
    out.close();
  }

}
