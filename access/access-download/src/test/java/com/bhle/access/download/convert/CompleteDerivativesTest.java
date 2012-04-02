package com.bhle.access.download.convert;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import com.bhle.access.jms.AfterBatchIngestJmsTest;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;

@ContextConfiguration
public class CompleteDerivativesTest extends AfterBatchIngestJmsTest {

	private static final Logger logger = LoggerFactory
			.getLogger(CompleteDerivativesTest.class);

	@Test
	public void testPostIngestDerivatives() throws InterruptedException {
		String msg = "{\"GUID\":\"a000test\", \"STATUS\":\"COMPLETED\"}";
		logger.debug("Send a message {}", msg);
		jmsProducer.send(msg);

		Thread.sleep(5000);

		try {
			testPdfGeneration();
			testJpegGeneration();
			testOcrGeneration();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void testPdfGeneration() throws MalformedURLException, IOException {
		FedoraURI fedoraURI = FedoraURI.getFedoraUri("a000test", "full_pdf");
		URI uri = StaticURI.toStaticFileUri(fedoraURI);
		InputStream in = uri.toURL().openStream();
		Assert.assertTrue(in.available() > 0);
		in.close();
	}
	
	private void testJpegGeneration() throws MalformedURLException, IOException {
		FedoraURI fedoraURI = FedoraURI.getFedoraUri("a000test", "full_jpeg");
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
