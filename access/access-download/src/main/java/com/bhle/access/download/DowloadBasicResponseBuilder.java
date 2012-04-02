package com.bhle.access.download;

import org.akubraproject.Blob;
import org.springframework.stereotype.Component;

@Component
public class DowloadBasicResponseBuilder implements DownloadResponseBuilder {
	@Override
	public DownloadResponse build(DownloadRequest request, Blob blob) {
		OnlineDownloadResponse response = new OnlineDownloadResponse();
		response.setRequest(request);
		return response;
	}

}
