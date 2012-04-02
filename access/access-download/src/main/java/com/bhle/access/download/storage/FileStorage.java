package com.bhle.access.download.storage;

import org.akubraproject.Blob;

import com.bhle.access.download.OfflineDownloadRequest;

public interface FileStorage {
	public Blob fetchBlob(OfflineDownloadRequest request);
	public Blob fetchBlob(String email, String filename);
}
