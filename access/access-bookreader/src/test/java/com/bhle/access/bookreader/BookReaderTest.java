package com.bhle.access.bookreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import com.bhle.access.bookreader.search.SearchService;
import com.bhle.access.jms.AfterBatchIngestJmsTest;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;

@ContextConfiguration
public class BookReaderTest extends AfterBatchIngestJmsTest {

	private static final Logger logger = LoggerFactory
			.getLogger(BookReaderTest.class);
	
	@Test
	public void testPostIngestDerivatives() throws InterruptedException {
		
		String msg = "{\"GUID\":\"a000test\", \"STATUS\":\"COMPLETED\"}";
		logger.debug("Send a message {}", msg);
		jmsProducer.send(msg);
		
		Thread.sleep(5000);
		
		try {
			testBookInfoGeneration();
			testThumbnailGeneration();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testThumbnailGeneration() throws MalformedURLException,
			IOException {
		FedoraURI fedoraURI = FedoraURI
				.getFedoraUri("a000test", "thumbnail");
		URI uri = StaticURI.toStaticFileUri(fedoraURI);
		InputStream in = uri.toURL().openStream();
		Assert.assertTrue(in.available() > 0);
		in.close();
	}

	public void testBookInfoGeneration() throws MalformedURLException,
			IOException {
		FedoraURI fedoraURI = FedoraURI.getFedoraUri("a000test",
				"bookreader");
		URI uri = StaticURI.toStaticFileUri(fedoraURI);
		InputStream in = uri.toURL().openStream();
		Assert.assertTrue(in.available() > 0);
		in.close();
	}

	@Ignore
	@Test
	public void testSearch() {
		String result = SearchService.query("a00000000000132805961115",
				"Standley");
		System.out.println(result);
	}
}
