package com.bhle.access.download.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.bhle.access.util.DjatokaURLBuilder;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.Resolution;
import com.bhle.access.util.StaticURI;

public class JpegPackageGenerator {

	private static final String DSID = "JP2";
	public static final String ENTRY_SUFFIX = "jpg";
	public static final String SUFFIX = "zip";

	public static byte[] generate(String[] pageURIs, Resolution resolution)
	throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		generate(pageURIs, resolution, byteOut);
		return byteOut.toByteArray();
	}
	
	public static void generate(String[] pageURIs, Resolution resolution, OutputStream out)
			throws IOException {
		ZipOutputStream zipOut = new ZipOutputStream(out);
		for (String pageURI : pageURIs) {
			AddPageEntry(zipOut, pageURI, resolution);
		}
	}

	private static void AddPageEntry(ZipOutputStream zipOut, String pageUri,
			Resolution resolution) {
		FedoraURI fedoraUri = new FedoraURI(URI.create(pageUri + "/" + DSID));
		URI uri = StaticURI.toStaticHttpUri(fedoraUri);

		ZipEntry entry;

		try {
			entry = new ZipEntry(fedoraUri.getGuid() + "_"
					+ fedoraUri.getSerialNumber() + "." + ENTRY_SUFFIX);
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
