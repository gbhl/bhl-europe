package com.bhle.access.download;

import org.akubraproject.Blob;

public interface DownloadResponseBuilder {
	public DownloadResponse build(DownloadRequest request, Blob blob); 
}
