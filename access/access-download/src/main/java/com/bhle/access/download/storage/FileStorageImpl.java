package com.bhle.access.download.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.UnsupportedIdException;
import org.springframework.stereotype.Component;

import com.bhle.access.download.OfflineDownloadRequest;

public class FileStorageImpl implements FileStorage {

	private BlobStore blobStore;

	public FileStorageImpl(BlobStore blobStore) {
		this.blobStore = blobStore;
	}

	@Override
	public Blob fetchBlob(OfflineDownloadRequest request) {
		return fetchBlob(request.getEmail(), request.generateFilename());
	}
	
	@Override
	public Blob fetchBlob(String email, String filename) {
		BlobStoreConnection connection = openConnection(blobStore);
		return getBlob(connection, email, filename);
	}

	private BlobStoreConnection openConnection(BlobStore blobStore) {
		try {
			return blobStore.openConnection(null, null);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Blob getBlob(BlobStoreConnection connection, String email,
			String filename) {
		try {
			return connection.getBlob(getInternalKey(email, filename), null);
		} catch (UnsupportedIdException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private URI getInternalKey(String email, String filename) {
		return URI.create(String.format("mailto:%s/%s", email, filename));
	}

	private OutputStream openOutputStream(Blob blob) {
		try {
			return blob.openOutputStream(-1, true);
		} catch (DuplicateBlobException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
