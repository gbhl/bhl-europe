package com.bhle.access.bookreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.bhle.access.BaseTest;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;

@ContextConfiguration
public class BookReaderTest extends BaseTest {

	@Test
	public void testPostIngestDerivatives() throws InterruptedException {
		Thread.sleep(15000);
		
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
}
