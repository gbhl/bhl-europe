package com.bhle.access.download;

import java.util.Comparator;

import org.springframework.integration.Message;

public class DownloadRequestPrioritizer implements
		Comparator<Message<DownloadRequest>> {

	@Override
	public int compare(Message<DownloadRequest> m1, Message<DownloadRequest> m2) {
		DownloadRequest r1 = m1.getPayload();
		DownloadRequest r2 = m2.getPayload();

		int w1 = calculateWeight(r1);
		int w2 = calculateWeight(r2);
		return w1 - w2;
	}

	private int calculateWeight(DownloadRequest request) {
		if (request.getContentType().equals(ContentType.OCR)) {
			return Integer.MAX_VALUE;
		} else {
			return Integer.MAX_VALUE - request.getPageURIs().length;
		}
	}

}
