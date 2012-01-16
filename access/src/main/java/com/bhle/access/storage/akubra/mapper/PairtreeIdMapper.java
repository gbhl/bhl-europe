package com.bhle.access.storage.akubra.mapper;

import java.net.URI;

import org.akubraproject.map.IdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.storage.akubra.mapper.file.FileMapper;
import com.bhle.access.storage.akubra.mapper.path.PathMapper;
import com.bhle.access.util.FedoraURI;

public class PairtreeIdMapper implements IdMapper {

	private static final Logger logger = LoggerFactory
			.getLogger(PairtreeIdMapper.class);

	private static final String internalScheme = "file";

	private PathMapper pathMapper;
	private FileMapper fileMapper;

	public PairtreeIdMapper(PathMapper pathMapper, FileMapper fileMapper) {
		this.pathMapper = pathMapper;
		this.fileMapper = fileMapper;
	}
	
	public PathMapper getPathMapper() {
		return pathMapper;
	}

	public FileMapper getFileMapper() {
		return fileMapper;
	}

	public URI getExternalId(URI internalId) throws NullPointerException {
		logger.info("Receive Internal ID: " + internalId);
		String externalPath = pathMapper.getExternalPath(internalId, FedoraURI.DEFAULT_NAMESPACE);
		String externalFile = fileMapper.getExternalFile(internalId);
		URI externalId = URI.create(FedoraURI.DEFAULT_SCHEME + ":fedora/" + FedoraURI.DEFAULT_NAMESPACE
				+ ":" + externalPath + "/" + externalFile);
		logger.info("Return External ID: " + externalId.toString());
		return externalId;
	}

	public URI getInternalId(URI externalId) throws NullPointerException {
		logger.info("Receive External ID: " + externalId);
		String internalPath = pathMapper.getInternalPath(externalId);
		String internalFile = fileMapper.getInternalFile(externalId);
		URI internalId = null;
		if (internalFile == null) {
			internalId = URI.create(internalScheme + ":" + internalPath);
		} else {
			internalId = URI.create(internalScheme + ":" + internalPath + "/"
					+ internalFile);
		}
		logger.info("Return Internal ID: " + internalId.toString());
		return internalId;
	}

	public String getInternalPrefix(String externalPrefix)
			throws NullPointerException {
		if (externalPrefix == null) {
			throw new NullPointerException();
		}
		return pathMapper.getInternalPrefix(externalPrefix);
	}

}
