package com.bhle.access.storage.akubra;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.akubraproject.Blob;
import org.akubraproject.BlobStore;
import org.akubraproject.impl.AbstractBlobStoreConnection;
import org.akubraproject.impl.StreamManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WildcardFSBlobStoreConnection extends AbstractBlobStoreConnection {
	private static final Logger log = LoggerFactory
			.getLogger(WildcardFSBlobStoreConnection.class);

	private final File baseDir;
	private final Set<File> modified;

	WildcardFSBlobStoreConnection(BlobStore blobStore, File baseDir,
			StreamManager manager, boolean noSync) {
		super(blobStore, manager);
		this.baseDir = baseDir;
		this.modified = noSync ? null : new HashSet<File>();
	}

	public Blob getBlob(URI blobId, Map<String, String> hints)
			throws IOException {
		ensureOpen();

		if (blobId == null)
			throw new UnsupportedOperationException();

		return new WildcardFSBlob(this, baseDir, blobId, streamManager,
				modified);
	}

	public Iterator<URI> listBlobIds(String filterPrefix) {
		ensureOpen();
		return new WildcardFSBlobIdIterator(baseDir, filterPrefix);
	}

	public void sync() throws IOException {
		ensureOpen();

		if (modified == null)
			throw new UnsupportedOperationException(
					"You promised you weren't going to call sync!");

		for (File f : modified) {
			try {
				FileInputStream fis = new FileInputStream(f);
				try {
					fis.getFD().sync();
				} finally {
					fis.close();
				}
			} catch (IOException ioe) {
				log.warn("Error sync'ing file '" + f + "'", ioe);
			}
		}

		modified.clear();
	}

	@Override
	public void close() {
		if (modified != null)
			modified.clear();

		super.close();
	}
}
