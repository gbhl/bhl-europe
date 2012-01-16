package com.bhle.access.storage.akubra;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.akubraproject.Blob;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.MissingBlobException;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.impl.AbstractBlob;
import org.akubraproject.impl.StreamManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.bhle.access.util.WildcardUtil;

class WildcardFSBlob extends AbstractBlob {
	static final String scheme = "file";
	private final URI canonicalId;
	private final File file;
	private final StreamManager manager;
	private final Set<File> modified;

	private final File baseDir;
	private String path;
	private boolean isWildcard;

	/**
	 * Create a file based blob
	 * 
	 * @param connection
	 *            the blob store connection
	 * @param baseDir
	 *            the baseDir of the store
	 * @param blobId
	 *            the identifier for the blob
	 * @param manager
	 *            the stream manager
	 * @param modified
	 *            the set of modified files in the connection; may be null
	 */
	WildcardFSBlob(WildcardFSBlobStoreConnection connection, File baseDir,
			URI blobId, StreamManager manager, Set<File> modified)
			throws UnsupportedIdException {
		super(connection, blobId);
		this.baseDir = baseDir;
		this.canonicalId = validateId(blobId);
		path = canonicalId.getRawSchemeSpecificPart();
		if (path.contains("*") || path.contains("?")) {
			this.isWildcard = true;
			this.file = null;
		} else {
			this.isWildcard = false;
			this.file = new File(baseDir, path);
		}

		this.manager = manager;
		this.modified = modified;
	}

	public URI getCanonicalId() {
		return canonicalId;
	}

	public InputStream openInputStream() throws IOException {
		if (isWildcard) {
			throw new UnsupportedOperationException(
					"WildcastFSBlob cannot be read.");
		}

		ensureOpen();

		if (!file.exists()) {
			System.out.println(file);
			throw new MissingBlobException(getId());
		}
		return manager.manageInputStream(getConnection(), new FileInputStream(
				file));
	}

	public OutputStream openOutputStream(long estimatedSize, boolean overwrite)
			throws IOException {
		if (isWildcard) {
			throw new UnsupportedOperationException(
					"WildcastFSBlob cannot be written.");
		}
		ensureOpen();

		if (!overwrite && file.exists())
			throw new DuplicateBlobException(getId());

		makeParentDirs(file);

		if (modified != null)
			modified.add(file);

		return manager.manageOutputStream(getConnection(),
				new FileOutputStream(file));
	}

	public long getSize() throws IOException {
		if (isWildcard) {
			throw new UnsupportedOperationException(
					"WildcastFSBlob cannot be moved.");
		}
		ensureOpen();

		if (!file.exists())
			throw new MissingBlobException(getId());

		return file.length();
	}

	public boolean exists() throws IOException {
		ensureOpen();
		if (isWildcard) {
			File[] files = WildcardUtil.listFiles(baseDir, path);
			return files.length != 0;
		} else {
			return file.exists();
		}
	}

	public void delete() throws IOException {
		ensureOpen();
		if (isWildcard) {
			File[] files = WildcardUtil.listFiles(baseDir, path);
			for (File file : files) {
				if (!file.delete() && file.exists())
					throw new IOException("Failed to delete file: " + file);
			}
		} else {
			if (file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				if (!file.delete() && file.exists())
					throw new IOException("Failed to delete file: " + file);

				if (modified != null)
					modified.remove(file);
			}
		}
	}

	public Blob moveTo(URI blobId, Map<String, String> hints)
			throws IOException {
		if (isWildcard) {
			throw new UnsupportedIdException(blobId,
					"WildcastFSBlob cannot be moved.");
		}

		ensureOpen();

		WildcardFSBlob dest = (WildcardFSBlob) getConnection().getBlob(blobId,
				hints);

		File other = dest.file;
		if (other.exists())
			throw new DuplicateBlobException(blobId);

		makeParentDirs(other);

		if (!file.renameTo(other)) {
			if (!file.exists())
				throw new MissingBlobException(getId());

			throw new IOException("Rename failed for an unknown reason.");
		}

		if (modified != null && modified.remove(file))
			modified.add(other);

		return dest;
	}

	static URI validateId(URI blobId) throws UnsupportedIdException {
		if (blobId == null)
			throw new NullPointerException("Id cannot be null");
		if (!blobId.getScheme().equalsIgnoreCase(scheme))
			throw new UnsupportedIdException(blobId, "Id must be in " + scheme
					+ " scheme");
		String path = blobId.getRawSchemeSpecificPart();
		if (path.startsWith("/"))
			throw new UnsupportedIdException(blobId,
					"Id must specify a relative path");
		try {
			// insert a '/' so java.net.URI normalization works
			URI tmp = new URI(scheme + ":/" + path);
			String nPath = tmp.normalize().getRawSchemeSpecificPart()
					.substring(1);
			if (nPath.equals("..") || nPath.startsWith("../"))
				throw new UnsupportedIdException(blobId,
						"Id cannot be outside top-level directory");
			return new URI(scheme + ":" + nPath);
		} catch (URISyntaxException wontHappen) {
			throw new Error(wontHappen);
		}
	}

	private static void makeParentDirs(File file) throws IOException {
		File parent = file.getParentFile();

		if (parent != null && !parent.exists() && !parent.mkdirs())
			throw new IOException("Unable to create parent directory: "
					+ parent.getPath());
	}
}
