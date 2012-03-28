package com.bhle.access.download;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DownloadResponseBuilderFactory {
	
	@Autowired
	private DownloadEmailResponseBuilder emailResponseBuilder;
	
	@Autowired
	private DowloadOnlineResponseBuilder onlineResponseBuilder;
	
	public DownloadResponseBuilder createBuilder(DownloadRequest request) {
		switch (request.getContentType()) {
		case JPEG:
			return emailResponseBuilder;
		case PDF:
			return emailResponseBuilder;
		default:
			return onlineResponseBuilder;
		}
	}
}
