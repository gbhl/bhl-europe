package com.bhle.access.download;

import java.util.concurrent.Future;

public interface DownloadGateway {
	public Future<DownloadResponse> download(DownloadRequest request);
}
