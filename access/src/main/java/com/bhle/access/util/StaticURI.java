package com.bhle.access.util;

import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.storage.akubra.mapper.PairtreeIdMapper;

public class StaticURI {
	
	private static final Logger logger = LoggerFactory
			.getLogger(StaticURI.class);
	
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

	public static URI toStaticHttpUri(FedoraURI fedoraUrl) {
		URI staticHttpUri = null;
		staticHttpUri = URI.create(BASE_URL + fedoraUrl.getGuid() + "/"
				+ ID_MAPPER.getFileMapper().getInternalFile(fedoraUrl.toURI()));
		return staticHttpUri;
	}

	public static URI toStaticFileUri(FedoraURI fedoraUrl) {
		URI staticFileUri = null;
		staticFileUri = URI.create("file:///"
				+ BAST_DIR
				+ "/"
				+ ID_MAPPER.getInternalId(fedoraUrl.toURI())
						.getSchemeSpecificPart());
		return staticFileUri;
	}

}
