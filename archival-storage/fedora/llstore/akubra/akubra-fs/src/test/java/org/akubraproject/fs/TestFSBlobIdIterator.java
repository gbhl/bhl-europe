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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for {@link FSBlobIdIterator}.
 *
 * @author Chris Wilper
 */
public class TestFSBlobIdIterator {
  private static File tmpDir;
  private static File emptyDir;
  private static File multiDir;

  /**
   * Sets up the test directories.
   *
   * @throws Exception if setup fails.
   */
  @BeforeClass
  public static void init() throws Exception {
    tmpDir = FSTestUtil.createTempDir();
    // setup dirs within for tests
    emptyDir = new File(tmpDir, "empty");
    emptyDir.mkdir();
    multiDir = new File(tmpDir, "multi");
    multiDir.mkdir();
    FSTestUtil.add(multiDir, "file-1");
    FSTestUtil.add(multiDir, "file-2");
    FSTestUtil.add(multiDir, "dir-empty/");
    FSTestUtil.add(multiDir, "dir-nonempty/");
    FSTestUtil.add(multiDir, "dir-nonempty/file-3");
    FSTestUtil.add(multiDir, "dir-nonempty/file-4");
    FSTestUtil.add(multiDir, "dir-nonempty/subdir/");
    FSTestUtil.add(multiDir, "dir-nonempty/subdir/file-5");
    FSTestUtil.add(multiDir, "dir-nonempty/subdir/file-6");
  }

  /**
   * Removes the test directories.
   */
  @AfterClass
  public static void destroy() {
    FSTestUtil.rmdir(tmpDir);
  }

  /**
   * An empty dir should result in an empty iterator.
   */
  @Test
  public void testEmpty() {
    assertEquals(getSet(getIter(emptyDir, null)).size(), 0);
  }

  /**
   * A populated dir should result in an iterator with an item for each file.
   */
  @Test
  public void testMulti() {
    assertEquals(getSet(getIter(multiDir, null)).size(), 6);
  }

  /**
   * Prefix filters should be respected.
   */
  @Test
  public void testMultiWithFilter() {
    String prefix = "file:";
    assertEquals(getSet(getIter(multiDir, prefix)).size(), 6);
    assertEquals(getSet(getIter(multiDir, prefix + "file-1")).size(), 1);
    assertEquals(getSet(getIter(multiDir, prefix + "dir-e")).size(), 0);
    assertEquals(getSet(getIter(multiDir, prefix + "dir-n")).size(), 4);
  }

  private static FSBlobIdIterator getIter(File dir, String filterPrefix) {
    return new FSBlobIdIterator(dir, filterPrefix);
  }

  private static Set<URI> getSet(Iterator<URI> iter) {
    HashSet<URI> set = new HashSet<URI>();
    while (iter.hasNext()) {
      set.add(iter.next());
    }
    return set;
  }

}
