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

/**
 * Utilities for testing the fs implementation.
 *
 * @author Chris Wilper
 */
public abstract class FSTestUtil {

  private static File targetDir = new File(System.getProperty("basedir"), "target");

  // create new test-temp dir in ${basedir}/target
  public static File createTempDir() throws Exception {
    File tmpDir = null;
    int i = 0;
    while (tmpDir == null || tmpDir.exists()) {
      tmpDir = new File(targetDir, "test-temp" + i++);
    }
    tmpDir.mkdir();
    return tmpDir;
  }

  // create an empty file or dir with the given name in the given dir
  public static void add(File dir, String name) throws Exception {
    File file = new File(dir, name);
    if (name.endsWith("/")) {
      file.mkdir();
    } else {
      file.createNewFile();
    }
  }

  // rm -rf dir
  public static void rmdir(File dir) {
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        rmdir(file);
      } else {
        file.delete();
      }
    }
    dir.delete();
  }

}
