package com.bhle.access.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.UnsupportedIdException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LowLevelStorageImpl implements LowLevelStorage {
	
	private static final Logger logger = LoggerFactory
			.getLogger(LowLevelStorageImpl.class);

	private BlobStore blobStore;

	public LowLevelStorageImpl(BlobStore blobStore) {
		this.blobStore = blobStore;
	}

	public void add(String pid, String dsId, InputStream in) throws IOException {
		BlobStoreConnection connection = openConnection(blobStore);
		Blob blob = getBlob(connection, pid, dsId);
		OutputStream out = openOutputStream(blob);
		IOUtils.copy(in, out);
		connection.close();
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

	private Blob getBlob(BlobStoreConnection connection, String pid, String dsId) {
		try {
			return connection.getBlob(getExternalKey(pid, dsId), null);
		} catch (UnsupportedIdException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	private URI getExternalKey(String pid, String dsId) {
		if (dsId == null) {
			return URI.create("info:fedora/" + pid);
		} else {
			return URI.create("info:fedora/" + pid + "/" + dsId);
		}
	}

	public void remove(String pid, String dsId) throws IOException {
		BlobStoreConnection connection = openConnection(blobStore);
		Blob blob = getBlob(connection, pid, dsId);
		if (blob.exists()) {
			blob.delete();
		}
		connection.close();
	}

	public void replace(String pid, String dsId, InputStream in)
			throws IOException {
		BlobStoreConnection connection = openConnection(blobStore);
		Blob blob = getBlob(connection, pid, dsId);
		OutputStream out = openOutputStream(blob);
		IOUtils.copy(in, out);
		connection.close();
	}

	public InputStream get(String pid, String dsId) throws IOException {
		BlobStoreConnection connection = openConnection(blobStore);
		Blob blob = getBlob(connection, pid, dsId);
		return blob.openInputStream();
	}

	public List<URI> list(String filterPrefix) throws IOException {
		List<URI> ids = new ArrayList<URI>();
		BlobStoreConnection connection = openConnection(blobStore);
		for (Iterator<URI> iterator = connection.listBlobIds(filterPrefix); iterator
				.hasNext();) {
			URI uri = iterator.next();
			ids.add(uri);
		}

		return ids;
	}

}
