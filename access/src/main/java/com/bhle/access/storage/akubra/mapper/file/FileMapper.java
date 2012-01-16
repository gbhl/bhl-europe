package com.bhle.access.storage.akubra.mapper.file;

import java.net.URI;

public interface FileMapper {
	public String getInternalFile(URI externalId);
	public String getExternalFile(URI internalId);
}
