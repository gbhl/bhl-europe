package com.bhle.access.download;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.bhle.access.util.Resolution;

@Ignore
@ContextConfiguration
public class DownloadFunctionalTest {

	private static final Logger logger = LoggerFactory
			.getLogger(DownloadFunctionalTest.class);

	@Autowired
	private DownloadGateway gateway;

	@Ignore
	@Test
	public void testDownload() {
		testDownloadPdf();
		testDownloadJpeg();
		testDownloadOcr();
	}

	public void testDownloadPdf() {
		OfflineDownloadRequest request = new OfflineDownloadRequest();
		request.setEmail("loylizh@gmail.com");
		request.setContentType(ContentType.PDF);
		request.setResolution(new Resolution("medium"));
		request.setPageURIs(new String[] { "info:fedora/"
				+ "bhle:10706-a000test-00001" });
		Future<DownloadResponse> futureResponse = gateway.download(request);
		try {
			OfflineDownloadResponse response = (OfflineDownloadResponse) futureResponse
					.get();
			Assert.assertTrue(response.getBlob().exists());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testDownloadJpeg() {
		OfflineDownloadRequest request = new OfflineDownloadRequest();
		request.setEmail("loylizh@gmail.com");
		request.setContentType(ContentType.JPEG);
		request.setResolution(new Resolution("medium"));
		request.setPageURIs(new String[] { "info:fedora/"
				+ "bhle:10706-a000test-00001" });
		Future<DownloadResponse> futureResponse = gateway.download(request);
		try {
			OfflineDownloadResponse response = (OfflineDownloadResponse) futureResponse
					.get();
			Assert.assertTrue(response.getBlob().exists());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testDownloadOcr() {
		BasicDownloadRequest request = new BasicDownloadRequest();
		request.setContentType(ContentType.OCR);
		request.setPageURIs(new String[] { "info:fedora/"
				+ "bhle:10706-a000test-00001" });
		Future<DownloadResponse> futureResponse = gateway.download(request);
		try {
			OnlineDownloadResponse response = (OnlineDownloadResponse) futureResponse
					.get();
			String ocr = new String(response.getBytes());
			Assert.assertTrue(ocr != null && !ocr.equals(""));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
