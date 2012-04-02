package com.bhle.access.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.akubraproject.Blob;

public interface LowLevelStorage {
	public void add(String pid, String dsId, InputStream in) throws IOException;

	public void remove(String pid, String dsId) throws IOException;

	public void replace(String pid, String dsId, InputStream in)
			throws IOException;

	public InputStream get(String pid, String dsId) throws IOException;

	public List<URI> list(String filterPrefix) throws IOException;
	
	public Blob getBlob(String pid, String dsId);
}
