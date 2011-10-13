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
package org.akubraproject.fs;

import java.io.File;

import java.net.URI;
import java.net.URISyntaxException;

import org.akubraproject.UnsupportedIdException;
import org.akubraproject.impl.StreamManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

/**
 * Unit tests for {@link FSBlob}.
 *
 * @author Chris Wilper
 */
public class TestFSBlob {
  private static URI id;

  private static File baseDir;
  private static FSBlobStoreConnection conn;
  private static StreamManager mgr;

  @BeforeClass
  public static void init() throws Exception {
    id = new URI("urn:example:store");
    baseDir = FSTestUtil.createTempDir();
    FSBlobStore store = new FSBlobStore(id, baseDir);
    mgr = new StreamManager();
    conn = new FSBlobStoreConnection(store, baseDir, mgr, true);
  }

  @AfterClass
  public static void destroy() {
    conn.close();
    FSTestUtil.rmdir(baseDir);
  }

  /**
   * Blobs that don't exist should report that they don't exist.
   */
  @Test
  public void testExistsFalse() throws Exception {
    assertFalse(getFSBlob("file:nonExistingPath").exists());
    assertFalse(getFSBlob("file:nonExistingPath/nonExistingPath").exists());
  }

  /**
   * Test that various valid blob ids are canonicalized correctly.
   */
  @Test
  public void testGetCanonicalId() {
    assertCanonical("file:foo");
    assertCanonical("file:foo/bar");

    // canonicalization should NOT unescape %-encoded chars
    assertCanonical("file:6a/1b/1e/info%3Afoo%2Fbar.baz%2E");

    assertCanonical("file:...");
    assertCanonical("file:.../foo");
    assertCanonical("file:foo/...");
    assertCanonical("file:foo/.../bar");

    assertCanonical("file:foo/../bar", "file:bar");
    assertCanonical("file:foo/bar/../../baz", "file:baz");
    assertCanonical("file:foo/../bar/../qux", "file:qux");

    assertCanonical("file:./foo", "file:foo");
    assertCanonical("file:foo/./bar", "file:foo/bar");
    assertCanonical("file:foo/bar/././baz", "file:foo/bar/baz");
    assertCanonical("file:foo/./bar/./qux", "file:foo/bar/qux");

    assertCanonical("FILE:foo", "file:foo");
    assertCanonical("file:foo//bar", "file:foo/bar");
    assertCanonical("file:foo///bar", "file:foo/bar");
  }

  /**
   * Test that various invalid blob ids fail at construction time.
   */
  @Test
  public void testConstructWithBadId() {
    assertNull(getFSBlob(null));
    assertNull(getFSBlob("urn:foo"));
    assertNull(getFSBlob("file:/foo"));
    assertNull(getFSBlob("file://foo"));
    assertNull(getFSBlob("file:///foo"));
    assertNull(getFSBlob("file:foo/"));
    assertNull(getFSBlob("file:.."));
    assertNull(getFSBlob("file:../"));
    assertNull(getFSBlob("file:foo/../../bar"));
  }

  private static FSBlob getFSBlob(String id) {
    try {
      URI uri = null;
      if (id != null)
        uri = new URI(id);
      return new FSBlob(conn, baseDir, uri, mgr, null);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    } catch (NullPointerException e) {
      return null;
    } catch (UnsupportedIdException e) {
      return null;
    }
  }

  private static void assertCanonical(String origId) {
    assertEquals(getCanonicalId(origId), origId);
  }

  private static void assertCanonical(String origId, String canonicalId) {
    assertEquals(getCanonicalId(origId), canonicalId);
  }

  private static String getCanonicalId(String id) {
    return getFSBlob(id).getCanonicalId().toString();
  }

}
