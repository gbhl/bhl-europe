package com.bhle.access.download.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;

import org.apache.commons.io.IOUtils;

import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;

public class OcrGenerator {

	private static final String DSID = "OCR";

	public static byte[] generate(String[] pageURIs) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		generate(pageURIs, byteOut);
		return byteOut.toByteArray();
	}

	public static void generate(String[] pageURIs, OutputStream out)
			throws IOException {
		for (String pageURI : pageURIs) {
			AddPageOcr(out, pageURI);
		}
	}

	private static void AddPageOcr(OutputStream out, String pageUri) {
		FedoraURI fedoraUri = new FedoraURI(URI.create(pageUri + "/" + DSID));
		URI uri = StaticURI.toStaticFileUri(fedoraUri);
		try {
			InputStream in = uri.toURL().openStream();
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(in);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
