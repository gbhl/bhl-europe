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
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transaction;

import org.testng.annotations.AfterSuite;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.tck.TCKTestSuite;

/**
 * TCK test suite for {@link FSBlobStore}.
 *
 * @author Ronald Tschalär
 */
public class TestFSBlobStoreTCK extends TCKTestSuite {
  private static File baseDir;

  public TestFSBlobStoreTCK() throws Exception {
    super(getStore(), getStoreId(), false, false);
  }

  private static URI getStoreId() throws Exception {
    return new URI("urn:example:store");
  }

  private static FSBlobStore getStore() throws Exception {
    baseDir = FSTestUtil.createTempDir();
    return new FSBlobStore(getStoreId(), baseDir);
  }

  @AfterSuite
  public void destroy() {
    FSTestUtil.rmdir(baseDir);
  }

  @Override
  protected URI createId(String name) {
    return URI.create("file:" + name);
  }

  @Override
  protected String getPrefixFor(String name) {
    return "file:" + name;
  }

  @Override
  protected URI getInvalidId() {
    return URI.create("urn:foo");
  }

  @Override
  protected URI[] getAliases(URI uri) {
    return new URI[] {
      uri,
      URI.create("file:foo/../" + uri.getSchemeSpecificPart()),
      URI.create("file:./" + uri.getSchemeSpecificPart()),
    };
  }

  @Override
  public void testSync() throws Exception {
    super.testSync();

    // test will-not-sync hint = true
    final Map<String, String> hints = new HashMap<String, String>();
    hints.put(FSBlobStore.WILL_NOT_SYNC, "true");

    shouldFail(new Action() {
      public void run(Transaction txn) throws Exception {
        BlobStoreConnection con = store.openConnection(txn, hints);
        con.sync();
      }
    }, UnsupportedOperationException.class, null);

    shouldFail(new Action() {
      public void run(Transaction txn) throws Exception {
        BlobStoreConnection con = store.openConnection(txn, hints);

        Blob b = getBlob(con, createId("blobConSync3"), null);
        createBlob(con, b, "foos");
        b = moveBlob(con, b, createId("blobConSync4"), "foos");
        deleteBlob(con, b);

        con.sync();
      }
    }, UnsupportedOperationException.class, null);

    // test will-not-sync hint = false
    hints.put(FSBlobStore.WILL_NOT_SYNC, "false");

    runTests(new Action() {
      public void run(Transaction txn) throws Exception {
        BlobStoreConnection con = store.openConnection(txn, hints);
        con.sync();
        con.close();
      }
    });
  }
}
