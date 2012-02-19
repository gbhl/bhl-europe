package com.bhle.access.storage.akubra;

import java.io.File;
import java.net.URI;
import java.util.Map;

import javax.transaction.Transaction;

import org.akubraproject.BlobStoreConnection;
import org.akubraproject.impl.AbstractBlobStore;
import org.akubraproject.impl.StreamManager;

public class WildcardFSBlobStore extends AbstractBlobStore {
	/**
	 * Connection hint indicating that the client will not call
	 * {@link BlobStoreConnection#sync}; the associated value must be "true"
	 * (case insesitive).
	 */
	public static final String WILL_NOT_SYNC = "org.akubraproject.will_not_sync";

	private final File baseDir;
	private final StreamManager manager = new StreamManager();

	/**
	 * Creates an instance with the given id and base storage directory.
	 * 
	 * @param id
	 *            the unique identifier of this blobstore.
	 * @param baseDir
	 *            the base storage directory.
	 */
	public WildcardFSBlobStore(URI id, File baseDir) {
		super(id);
		this.baseDir = baseDir;
	}

	public BlobStoreConnection openConnection(Transaction tx,
			Map<String, String> hints) {
		if (tx != null) {
			throw new UnsupportedOperationException();
		}

		boolean no_sync = (hints != null)
				&& Boolean.parseBoolean(hints.get(WILL_NOT_SYNC));
		return new WildcardFSBlobStoreConnection(this, baseDir, manager, no_sync);
	}
}
