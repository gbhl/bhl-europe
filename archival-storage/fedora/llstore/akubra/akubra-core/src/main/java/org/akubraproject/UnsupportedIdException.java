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
package org.akubraproject;

import java.net.URI;

/**
 * This exception is thrown by a blob store to indicate that the application
 * supplied blob identifier is outside the set of all possible identifiers 
 * for blobs in this store. This exception is not to be interpreted as a 
 * missing/non-existent blob.
 *
 * @author Pradeep Krishnan
 */
public class UnsupportedIdException extends AkubraBlobException {
  public static final long serialVersionUID = 1L;

  /**
   * Construct an instance for the given unsupported identifier with no further details.
   *
   * @param blobId the unsupported blob identifier
   */
  public UnsupportedIdException(URI blobId) {
    this(blobId, "", null);
  }

  /**
   * Construct an instance for the given unsupported identifier with a detailed message.
   *
   * @param blobId the unsupported blob identifier
   * @param msg    the details about the exception
   */
  public UnsupportedIdException(URI blobId, String msg) {
    this(blobId, msg, null);
  }

  /**
   * Construct an instance for the given unsupported identifier with a detailed message
   * and the underlying cause.
   *
   * @param blobId the unsupported blob identifier
   * @param msg    the details about the exception
   * @param cause  the underlying exception that caused this exception
   */
  public UnsupportedIdException(URI blobId, String msg, Throwable cause) {
    super(blobId, "(Unsupported blob identifier = '" + blobId + "')" + msg, cause);
  }
}
