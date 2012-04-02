package com.bhle.access.download;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DownloadResponseBuilderFactory {

	@Autowired
	private DownloadEmailResponseBuilder emailResponseBuilder;

	@Autowired
	private DowloadBasicResponseBuilder basicResponseBuilder;

	public DownloadResponseBuilder createBuilder(DownloadRequest request) {
		if (request.isOffline()) {
			return emailResponseBuilder;
		} else {
			return basicResponseBuilder;
		}
	}
}
