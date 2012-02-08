package com.bhle.access.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;

public class OcrGenerator {
	
	private static final String DSID = "OCR";

	public static byte[] generate(String[] pageURIs) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		for (String pageURI : pageURIs) {
			AddPageOcr(byteOut, pageURI);
		}
		return byteOut.toByteArray();
	}

	private static void AddPageOcr(ByteArrayOutputStream byteOut, String pageUri) {
		FedoraURI fedoraUri = new FedoraURI(URI.create(pageUri + "/" + DSID));
		URI uri = StaticURI.toStaticHttpUri(fedoraUri);
		try {
			IOUtils.copy(uri.toURL().openStream(), byteOut);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
