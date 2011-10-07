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

import java.net.URI;

import org.akubraproject.BlobStoreConnection;

/**
 * Maps internal blob ids to external blob ids and vice-versa.
 *
 * @author Chris Wilper
 */
public interface IdMapper {

  /**
   * Gets the appropriate external id for an internal id.
   *
   * @param internalId the internal id, never <code>null</code>.
   * @return the external id, never <code>null</code>.
   * @throws NullPointerException if <var>internalId</var> is <code>null</code>.
   */
  URI getExternalId(URI internalId) throws NullPointerException;

  /**
   * Gets the appropriate internal id for an external id.
   *
   * @param externalId the external id, never <code>null</code>.
   * @return the internal id, never <code>null</code>.
   * @throws NullPointerException if <var>externalId</var> is <code>null</code>.
   */
  URI getInternalId(URI externalId) throws NullPointerException;

  /**
   * Gets the internal id prefix for an external id prefix, if known.
   * Implementations should provide this information whenever possible
   * because it supports a more efficient implementation of
   * {@link BlobStoreConnection#listBlobIds(String)} when a
   * <var>filterPrefix</var> is specified.
   *
   * @param externalPrefix the external prefix, never <code>null</code>.
   * @return the internal prefix, or <code>null</code> if unknown.
   * @throws NullPointerException if <var>externalPrefix</var> is <code>null</code>.
   */
  String getInternalPrefix(String externalPrefix) throws NullPointerException;

}
