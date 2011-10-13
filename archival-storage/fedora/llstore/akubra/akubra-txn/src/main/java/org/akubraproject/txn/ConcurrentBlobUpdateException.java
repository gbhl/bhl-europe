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
package org.akubraproject.txn;

import java.net.URI;
import org.akubraproject.AkubraBlobException;

/**
 * This exception is thrown by a transactional blob store when the same blob is created, deleted,
 * or modified by two separate concurrent transactions.
 *
 * @author Ronald Tschalär
 */
public class ConcurrentBlobUpdateException extends AkubraBlobException {
  public static final long serialVersionUID = 1L;

  /**
   * Exception indicating there is duplicate blob in the store associated with the blob-id.
   *
   * @param blobId the blob-id
   */
  public ConcurrentBlobUpdateException(URI blobId) {
    this(blobId, "Concurrent modification of blob with id = '" + blobId + "'", null);
  }

  /**
   * Exception indicating there is duplicate blob in the store associated with the blob-id.
   *
   * @param blobId the blob-id
   * @param msg the details about the exception
   */
  public ConcurrentBlobUpdateException(URI blobId, String msg) {
    this(blobId, msg, null);
  }

  /**
   * Exception indicating there is duplicate blob in the store associated with the blob-id.
   *
   * @param blobId the blob-id
   * @param msg    the details about the exception
   * @param cause  the underlying exception that caused this exception
   */
  public ConcurrentBlobUpdateException(URI blobId, String msg, Throwable cause) {
    super(blobId, msg, cause);
  }
}
