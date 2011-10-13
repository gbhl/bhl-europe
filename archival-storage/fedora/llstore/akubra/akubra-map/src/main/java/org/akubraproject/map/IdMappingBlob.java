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

import java.io.IOException;

import java.net.URI;

import java.util.Map;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.akubraproject.impl.BlobWrapper;

/**
 * Wraps an existing {@link Blob} to provide id mapping where appropriate.
 *
 * @author Chris Wilper
 */
class IdMappingBlob extends BlobWrapper {
  private final IdMapper mapper;

  public IdMappingBlob(BlobStoreConnection connection, Blob delegate, IdMapper mapper) {
    super(delegate, connection);
    this.mapper = mapper;
  }

  @Override
  public URI getCanonicalId() throws IOException {
    URI internalId = delegate.getCanonicalId();
    if (internalId == null)
      return null;
    return mapper.getExternalId(internalId);
  }

  @Override
  public URI getId() {
    return mapper.getExternalId(delegate.getId());
  }

  @Override
  public Blob moveTo(URI blobId, Map<String, String> hints) throws DuplicateBlobException,
      IOException, MissingBlobException, NullPointerException, IllegalArgumentException {
    Blob internalBlob;
    if (blobId == null)
      internalBlob = delegate.moveTo(null, hints);
    else
      internalBlob = delegate.moveTo(mapper.getInternalId(blobId), hints);
    return new IdMappingBlob(owner, internalBlob, mapper);
  }

}
