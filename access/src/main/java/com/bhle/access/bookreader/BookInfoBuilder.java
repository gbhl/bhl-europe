package com.bhle.access.bookreader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;

import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.download.OcrGenerator;
import com.bhle.access.util.DjatokaURLBuilder;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.FedoraUtil;
import com.bhle.access.util.Olef;
import com.bhle.access.util.StaticURI;

public class BookInfoBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(BookInfoBuilder.class);

	public static BookInfo build(String guid) {
		BookInfo book = buildBookInfo(guid);
		buildPageInfo(book, guid);
		buildTableOfContent(book, guid);
		return book;
	}

	private static BookInfo buildBookInfo(String guid) {
		BookInfo book = new BookInfo();
		book.setGuid(guid);
		Olef olef = getOlef(guid);
		book.setOlef(olef);
		book.setTitle(olef.getTitle());
		setEntryPage(book, olef);
		return book;
	}

	private static void buildTableOfContent(BookInfo book, String guid) {
	}

	private static void buildPageInfo(BookInfo book, String guid) {
		Olef olef = book.getOlef();
		String[] pageUris = FedoraUtil.getAllMembers(
				FedoraURI.getPidFromGuid(guid), FedoraUtil.PAGE_MODEL);
		Arrays.sort(pageUris);
		for (int i = 0; i < pageUris.length; i++) {
			PageInfo page = new PageInfo();
			page.setIndex(i);
			page.setName(olef.getPageName(i));
			setPageUrl(pageUris[i], page);
			setPageWidthAndHeight(pageUris[i], page);
			page.setScientificNames(olef.getScientificNames(i));
			setPageOcr(pageUris[i], page);
			book.addPage(page);
		}

	}

	private static void setPageOcr(String pageUri, PageInfo page) {
		FedoraURI fedoraUri = new FedoraURI(URI.create(pageUri + "/OCR"));
		URI staticUri = StaticURI.toStaticHttpUri(fedoraUri);
		page.setOcrUrl(staticUri.toString());
	}

	private static void setEntryPage(BookInfo book, Olef olef) {
		String entryPage = olef.getEntryPage();
		if (entryPage.equals("")) {
			book.setEntryPageIndex(0);
		} else {
			book.setEntryPageIndex(Integer.valueOf(olef.getEntryPage()));
		}
	}

	private static void setPageUrl(String pageUri, PageInfo page) {
		try {
			URI pageHttpUri = getPageHttpPath(pageUri);
			page.setUrl(DjatokaURLBuilder.build(pageHttpUri.toURL()).toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private static void setPageWidthAndHeight(String pageUri, PageInfo page) {
		File pageFile = getPageFilePath(pageUri);
		try {
			Info info = new Info(pageFile.getAbsolutePath(), true);
			page.setHeight(info.getImageHeight());
			page.setWidth(info.getImageWidth());
		} catch (InfoException e) {
			e.printStackTrace();
		}
	}

	private static File getPageFilePath(String pageUri) {
		FedoraURI fedoraUri = new FedoraURI(URI.create(pageUri + "/JP2"));
		File file = new File(StaticURI.toStaticFileUri(fedoraUri));
		return file;
	}

	private static URI getPageHttpPath(String pageUri) {
		FedoraURI fedoraUri = new FedoraURI(URI.create(pageUri + "/JP2"));
		return StaticURI.toStaticHttpUri(fedoraUri);
	}

	private static Olef getOlef(String guid) {
		try {
			FedoraURI olefUri = FedoraURI.getFedoraUri(guid, "OLEF");
			URI olefHttpUri = StaticURI.toStaticHttpUri(olefUri);
			return new Olef(olefHttpUri.toURL());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
