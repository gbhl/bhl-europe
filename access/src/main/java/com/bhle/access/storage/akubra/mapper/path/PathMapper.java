package com.bhle.access.storage.akubra.mapper.path;

import java.net.URI;

public interface PathMapper {
	public String getInternalPath(URI externalId);
	public String getExternalPath(URI internalId, String namespace);
	public String getInternalPrefix(String externalPrefix);
}
