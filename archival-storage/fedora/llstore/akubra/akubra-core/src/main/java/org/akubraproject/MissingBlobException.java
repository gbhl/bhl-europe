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
 * This exception is thrown by a blob store in case in of missing blob in store.
 *
 * @author Chris Wilper
 * @author Pradeep Krishnan
 * @author Ronald Tschalär
 */
public class MissingBlobException extends AkubraBlobException {
  public static final long serialVersionUID = 1L;

  /**
   * Exception indicating the there is no blob in the store associated with the blob-id.
   *
   * @param blobId the blob-id
   */
  public MissingBlobException(URI blobId) {
    this(blobId, "", null);
  }

  /**
   * Exception indicating the there is no blob in the store associated with the blob-id.
   *
   * @param blobId the blob-id
   * @param msg the details about the exception
   */
  public MissingBlobException(URI blobId, String msg) {
    this(blobId, msg, null);
  }

  /**
   * Exception indicating the there is no blob in the store associated with the blob-id.
   *
   * @param blobId the blob-id
   * @param msg    the details about the exception
   * @param cause  the underlying exception that caused this exception
   */
  public MissingBlobException(URI blobId, String msg, Throwable cause) {
    super(blobId, "(Missing blob with id = '" + blobId + "')" + msg, cause);
  }
}
