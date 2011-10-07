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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * A simple, dynamic in-memory buffer to which we can write and from which we can read.
 *
 * @author Ronald Tschalär
 */
class MemData extends ByteArrayOutputStream {
  /**
   * Create a new buffer of the given size.
   *
   * @param estimatedSize estimated size of the data this buffer will hold
   */
  public MemData(int estimatedSize) {
    super(estimatedSize);
  }

  /**
   * Get an input stream on the current data. A new stream starting at the beggining of the
   * data is created each time this method is invoked.
   *
   * @return the input stream
   */
  public InputStream getInputStream() {
    return new ByteArrayInputStream(buf, 0, count);
  }

  /**
   * The size of the internal buffer.
   *
   * @return the buffer size
   */
  public int bufferSize() {
    return buf.length;
  }
}
