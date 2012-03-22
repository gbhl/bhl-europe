package com.bhle.access.download;

import java.io.IOException;

import org.akubraproject.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bhle.access.download.storage.FileStorage;

@Component
public class OfflineDownloadResponssFetcher {
	@Autowired
	private FileStorage storage;

	public OfflineDownloadResponse fetch(String email, String filename)
			throws IOException {
		OfflineDownloadResponse response = new OfflineDownloadResponse();
		Blob blob = storage.getBlob(email, filename);
		if (blob.exists()) {
			response.setBlob(storage.getBlob(email, filename));
		} else {
			throw new IOException("Content not found");
		}
		return response;
	}
}
