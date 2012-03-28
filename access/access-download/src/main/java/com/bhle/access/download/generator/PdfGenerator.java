package com.bhle.access.download.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.download.Resolution;
import com.bhle.access.util.DjatokaURLBuilder;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfGenerator {

	private static final Logger logger = LoggerFactory
			.getLogger(PdfGenerator.class);
	
	private static final String DSID = "JP2";
	public static final String SUFFIX = "pdf";

	public static byte[] generate(String[] pageURIs, Resolution resolution) throws IOException{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		generate(pageURIs, resolution, bao);
		return bao.toByteArray();
	}
	
	public static void generate(String[] pageURIs, Resolution resolution, OutputStream out)
			throws IOException {
		Document document = initialPDF();

		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			mergeAllPages(pageURIs, resolution, document);
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

	private static Document initialPDF() {
		Document document = new Document();
		document.setMargins(0, 0, 0, 0);
		return document;
	}

	private static void mergeAllPages(String[] pageURIs, Resolution resolution,
			Document document) {
		for (int i = 0; i < pageURIs.length; i++) {
			try {
				FedoraURI fedoraUri = new FedoraURI(URI.create(pageURIs[i]
						+ "/" + DSID));
				URI uri = StaticURI.toStaticHttpUri(fedoraUri);
				URL djatokaURL = DjatokaURLBuilder.build(uri.toURL(),
						resolution.getLevel());
				Image image = Image.getInstance(djatokaURL);
				
				configureImage(image, document);
				document.add(image);
//				if (i != pageURIs.length - 1) {
					document.newPage();
//				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (BadElementException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
	}

	private static void configureImage(Image image, Document document) {
		image.setAlignment(Element.ALIGN_CENTER);
		image.scaleToFit(document.getPageSize().getWidth(), document
				.getPageSize().getHeight());
	}

}
