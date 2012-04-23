package com.bhle.access;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.bhle.access.bookreader.search.SearchService;
import com.bhle.access.download.BasicDownloadRequest;
import com.bhle.access.download.ContentType;
import com.bhle.access.download.DownloadGateway;
import com.bhle.access.download.DownloadResponse;
import com.bhle.access.download.OfflineDownloadRequest;
import com.bhle.access.download.OfflineDownloadResponse;
import com.bhle.access.download.OnlineDownloadResponse;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.Resolution;
import com.bhle.access.util.StaticURI;

@ContextConfiguration
public class DerivativesFunctionalTest extends BaseTest {

	private static final Logger logger = LoggerFactory
			.getLogger(DerivativesFunctionalTest.class);

	@Autowired
	private DownloadGateway gateway;

	@Test
	public void testExistenceOfDerivatives() throws IOException,
			InterruptedException {
		Thread.sleep(30000);

		testBookInfoGeneration();
		testThumbnailGeneration();
		testSearch();

		testPdfGeneration();
		testJpegGeneration();
		testOcrGeneration();
	}

	public void testThumbnailGeneration() throws MalformedURLException,
			IOException {
		FedoraURI fedoraURI = FedoraURI.getFedoraUri("a000test", "thumbnail");
		URI uri = StaticURI.toStaticFileUri(fedoraURI);
		InputStream in = uri.toURL().openStream();
		Assert.assertTrue(in.available() > 0);
		in.close();
	}

	public void testBookInfoGeneration() throws MalformedURLException,
			IOException {
		FedoraURI fedoraURI = FedoraURI.getFedoraUri("a000test", "bookreader");
		URI uri = StaticURI.toStaticFileUri(fedoraURI);
		InputStream in = uri.toURL().openStream();
		Assert.assertTrue(in.available() > 0);
		in.close();
	}

	public void testSearch() {
		String json = SearchService.query("a000test", "Test OCR 2");
		logger.debug(json);
	}

	private void testPdfGeneration() throws MalformedURLException, IOException {
		FedoraURI fedoraURI = FedoraURI.getFedoraUri("a000test", "full_pdf");
		URI uri = StaticURI.toStaticFileUri(fedoraURI);
		InputStream in = uri.toURL().openStream();
		Assert.assertTrue(in.available() > 0);
		in.close();
	}

	private void testJpegGeneration() throws MalformedURLException, IOException {
		FedoraURI fedoraURI = FedoraURI.getFedoraUri("a000test", "full_jpg");
		URI uri = StaticURI.toStaticFileUri(fedoraURI);
		InputStream in = uri.toURL().openStream();
		Assert.assertTrue(in.available() > 0);
		in.close();
	}

	private void testOcrGeneration() throws MalformedURLException, IOException {
		FedoraURI fedoraURI = FedoraURI.getFedoraUri("a000test", "full_ocr");
		URI uri = StaticURI.toStaticFileUri(fedoraURI);
		InputStream in = uri.toURL().openStream();
		Assert.assertTrue(in.available() > 0);
		in.close();
	}
}
