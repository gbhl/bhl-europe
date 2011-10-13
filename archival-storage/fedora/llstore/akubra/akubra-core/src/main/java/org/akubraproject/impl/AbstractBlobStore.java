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

import java.net.URI;

import org.akubraproject.BlobStore;

/**
 * An abstract BlobStore implementation.
 *
 * @author Chris Wilper
 */
public abstract class AbstractBlobStore implements BlobStore {
  /** This store's id */
  protected final URI id;

  /**
   * Create a new blob store.
   *
   * @param id the store's id
   */
  protected AbstractBlobStore(URI id) {
    this.id = id;
  }

  @Override
  public URI getId() {
    return id;
  }
}
