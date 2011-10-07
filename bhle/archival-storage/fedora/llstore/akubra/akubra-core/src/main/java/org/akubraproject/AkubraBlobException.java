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
 * A common superclass for exceptions that relate to a specific blob.
 *
 * @author Chris Wilper
 * @author Pradeep Krishnan
 * @author Ronald Tschalär
 */
public abstract class AkubraBlobException extends AkubraException {
  public static final long serialVersionUID = 1L;
  private final URI blobId;

  /**
   * Create a new exception instance.
   *
   * @param blobId the blob-id
   * @param msg    the details about the exception
   * @param cause  the underlying exception that caused this exception; may be null
   */
  protected AkubraBlobException(URI blobId, String msg, Throwable cause) {
    super(msg != null ? msg : cause != null ? cause.toString() : null, cause);
    this.blobId = blobId;
  }

  /**
   * Return the blob-id
   *
   * @return the blob-id
   */
  public URI getBlobId() {
    return blobId;
  }
}
