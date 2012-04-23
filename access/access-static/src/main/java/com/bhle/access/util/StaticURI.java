package com.bhle.access.util;

import java.net.URI;
import java.net.URL;

import com.bhle.access.storage.akubra.mapper.PairtreeIdMapper;

public class StaticURI {
	
	public static URL BASE_URL;

	public static String BAST_DIR;

	private static PairtreeIdMapper ID_MAPPER;

	public void setBaseUrl(URL baseUrl) {
		StaticURI.BASE_URL = baseUrl;
	}

	public void setBaseDir(String baseDir) {
		StaticURI.BAST_DIR = baseDir;
	}

	public void setIdMapper(PairtreeIdMapper idMapper) {
		StaticURI.ID_MAPPER = idMapper;
	}

	public static URI toStaticHttpUri(FedoraURI fedoraUri) {
		URI staticHttpUri = null;
		staticHttpUri = URI.create(BASE_URL + fedoraUri.getGuid() + "/"
				+ ID_MAPPER.getFileMapper().getInternalFile(fedoraUri.toURI()));
		return staticHttpUri;
	}

	public static URI toStaticFileUri(FedoraURI fedoraUri) {
		URI staticFileUri = null;
		staticFileUri = URI.create("file:///"
				+ BAST_DIR
				+ "/"
				+ ID_MAPPER.getInternalId(fedoraUri.toURI())
						.getSchemeSpecificPart());
		return staticFileUri;
	}

}
