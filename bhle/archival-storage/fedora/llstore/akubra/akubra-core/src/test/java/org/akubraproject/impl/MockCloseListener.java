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

import java.io.Closeable;

import java.util.HashSet;
import java.util.Set;

/**
 * Mock <code>CloseListener</code> for testing.
 *
 * @author Chris Wilper
 */
public class MockCloseListener implements CloseListener {

  private final Set<Closeable> closedSet = new HashSet<Closeable>();

  @Override
  public void notifyClosed(Closeable closeable) {
    closedSet.add(closeable);
  }

  /**
   * Gets the set of objects that have been closed so far.
   *
   * @return the set.
   */
  public Set<Closeable> getClosedSet() {
    return closedSet;
  }

}
