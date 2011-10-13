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

package org.akubraproject.mem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.transaction.Transaction;

import org.akubraproject.BlobStoreConnection;
import org.akubraproject.impl.AbstractBlobStore;
import org.akubraproject.impl.StreamManager;

/**
 * Simple in-memory blob store.
 *
 * @author Ronald Tschalär
 */
public class MemBlobStore extends AbstractBlobStore {
  private static final Random rng = new Random();

  private final Map<URI, MemData> blobs     = new HashMap<URI, MemData>();
  private final StreamManager     streamMgr = new StreamManager();

  /**
   * Create a new, random ID with the given prefix.
   *
   * @param prefix the URI prefix to use
   * @return the new ID
   */
  static synchronized URI getRandomId(String prefix) {
    return URI.create(prefix + rng.nextLong());
  }

  /**
   * Create a new blob-store with a random id.
   */
  public MemBlobStore() {
    this(getRandomId("urn:mem-blob-store:"));
  }

  /**
   * Create a new blob-store.
   *
   * @param id the store id
   */
  public MemBlobStore(URI id) {
    super(id);
  }

  @Override
  public BlobStoreConnection openConnection(Transaction tx, Map<String, String> hints)
      throws UnsupportedOperationException {
    if (tx != null)
      throw new UnsupportedOperationException("MemBlobStore does not support transactions");

    return new MemConnection(this, blobs, streamMgr);
  }
}
