package com.bhle.access.download;

import org.akubraproject.Blob;
import org.springframework.stereotype.Component;

@Component
public class DowloadOnlineResponseBuilder implements DownloadResponseBuilder {
	@Override
	public DownloadResponse build(DownloadRequest request, Blob blob,
			byte[] bytes) {
		OnlineDownloadResponse response = new OnlineDownloadResponse();
		response.setRequest(request);
		response.setBytes(bytes);
		return response;
	}

}
