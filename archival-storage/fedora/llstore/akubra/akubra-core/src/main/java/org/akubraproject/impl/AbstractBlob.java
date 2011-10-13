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

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;

/**
 * An abstract base class for Blobs.
 *
 * @author Pradeep Krishnan
 */
public abstract class AbstractBlob implements Blob {
  /** The connection that created this blob.*/
  protected final BlobStoreConnection owner;

  /** The non-null id of this blob.*/
  protected final URI id;

  /**
   * Creates a new AbstractBlob object.
   *
   * @param owner the connection that owns this blob (could be null for store-owned)
   * @param id the id of this blob
   */
  protected AbstractBlob(BlobStoreConnection owner, URI id) {
    this.owner = owner;
    this.id    = id;
  }

  @Override
  public BlobStoreConnection getConnection() {
    return owner;
  }

  @Override
  public URI getId() {
    return id;
  }

  @Override
  public URI getCanonicalId() throws IOException {
    return null;
  }

  /**
   * Helper that checks whether the connection is open and throws an exception if not.
   *
   * @throws IllegalStateException if the connection has been closed
   */
  protected void ensureOpen() throws IllegalStateException {
    if (getConnection().isClosed())
      throw new IllegalStateException("Connection closed.");
  }

  /**
   * Indicates whether some other object is "equal to" this one. For the other object to
   * be considered equal, it must be an instance of Blob and it must have the same id
   * as this Blob and it must also have the same owning BlobStoreConnection as this Blob.
   * Override in sub-classes if that is not the desired behavior.
   *
   * @param obj the reference object with which to compare
   *
   * @return true if this object is the same as the obj argument; false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Blob) &&
      same(getId(), ((Blob) obj).getId()) && same(getConnection(), ((Blob) obj).getConnection());
  }

  /**
   * Null-safe equals evaluation.
   *
   * @param o1 first object (could be null)
   * @param o2 second object (could be null)
   *
   * @return true if the objects are the same as reported by {@link java.lang.Object#equals equals}
   *              in a null-safe manner
   */
  public static boolean same(Object o1, Object o2) {
    return (o1 == null) ? (o2 == null) : o1.equals(o2);
  }

  /**
   * Returns a hash code value for this Blob. This implementation returns the hash code of the id.
   * Override in sub-classes if that is not the desired behavior.
   *
   * @return a hash code value for this Blob
   */
  @Override
  public int hashCode() {
    return getId().hashCode();
  }
}
