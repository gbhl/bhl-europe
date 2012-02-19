package com.bhle.access.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.bhle.access.util.DjatokaURLBuilder;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;

public class JpegPackageGenerator {

	private static final String DSID = "JP2";
	private static final String SUFFIX = "jpg";

	public static byte[] generate(String[] pageURIs, Resolution resolution)
			throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ZipOutputStream zipOut = new ZipOutputStream(byteOut);
		for (String pageURI : pageURIs) {
			AddPageEntry(zipOut, pageURI, resolution);
		}
		return byteOut.toByteArray();
	}

	private static void AddPageEntry(ZipOutputStream zipOut, String pageUri,
			Resolution resolution) {
		FedoraURI fedoraUri = new FedoraURI(URI.create(pageUri + "/" + DSID));
		URI uri = StaticURI.toStaticHttpUri(fedoraUri);

		ZipEntry entry;

		try {
			entry = new ZipEntry(fedoraUri.getGuid() + "_"
					+ fedoraUri.getSerialNumber() + "." + SUFFIX);
			zipOut.putNextEntry(entry);
			URL djatokaURL = DjatokaURLBuilder.build(uri.toURL(), resolution.getLevel());
			InputStream in = djatokaURL.openStream();
			IOUtils.copy(in, zipOut);
			zipOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
