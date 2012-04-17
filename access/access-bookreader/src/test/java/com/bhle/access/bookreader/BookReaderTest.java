package com.bhle.access.bookreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import com.bhle.access.BaseTest;
import com.bhle.access.bookreader.search.SearchResult;
import com.bhle.access.bookreader.search.SearchService;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;

@ContextConfiguration
public class BookReaderTest extends BaseTest {

	private static final Logger logger = LoggerFactory
			.getLogger(BookReaderTest.class);

	@Test
	public void testPostIngestDerivatives() throws InterruptedException {
		Thread.sleep(15000);

		try {
			testBookInfoGeneration();
			testThumbnailGeneration();
			testSearch();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
