package com.bhle.access.download.offline.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.UnsupportedIdException;
import org.apache.commons.io.IOUtils;

import com.bhle.access.download.offline.OfflineProduct;

public class OfflineStorageImpl implements OfflineStorage {

	private BlobStore blobStore;

	public OfflineStorageImpl(BlobStore blobStore) {
		this.blobStore = blobStore;
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

	private Blob getBlob(BlobStoreConnection connection, OfflineProduct product) {
		try {
			return connection.getBlob(getInternalKey(product), null);
		} catch (UnsupportedIdException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private URI getInternalKey(OfflineProduct product) {
		return URI.create(String.format("mailto:%s/%s", product.getEmail(), product.getFileName()));
	}

	public void save(OfflineProduct product) throws IOException {
		BlobStoreConnection connection = openConnection(blobStore);
		Blob blob = getBlob(connection, product);
		OutputStream out = openOutputStream(blob);
		IOUtils.copy(product.getInputStream(), out);
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

}
