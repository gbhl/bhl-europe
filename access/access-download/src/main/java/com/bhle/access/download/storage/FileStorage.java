package com.bhle.access.download.storage;

import org.akubraproject.Blob;

import com.bhle.access.download.OfflineDownloadRequest;

public interface FileStorage {
	public Blob getBlob(OfflineDownloadRequest request);
	public Blob getBlob(String email, String filename);
}
