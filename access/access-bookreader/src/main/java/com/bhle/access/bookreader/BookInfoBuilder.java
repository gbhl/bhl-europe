package com.bhle.access.bookreader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.domain.Derivative;
import com.bhle.access.storage.StorageService;
import com.bhle.access.util.DjatokaURLBuilder;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.FedoraUtil;
import com.bhle.access.util.ImageUtil;
import com.bhle.access.util.Olef;
import com.bhle.access.util.StaticURI;

public class BookInfoBuilder {

	private static StorageService storageService;

	public void setStorageService(StorageService storageService) {
		BookInfoBuilder.storageService = storageService;
	}

	private static String DSID = "BOOKREADER";

	private static String GUID_BANK_ID;

	public void setGuidBankId(String bankId) {
		BookInfoBuilder.GUID_BANK_ID = bankId;
	}

	private static URL DOMAIN_NAME;

	public void setDomainName(String domain) {
		try {
			BookInfoBuilder.DOMAIN_NAME = new URL(domain);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private static final Logger logger = LoggerFactory
			.getLogger(BookInfoBuilder.class);

	public static BookInfo build(String guid) throws IOException {
		logger.debug("Build BookReader Information");

		BookInfo book = buildBookInfo(guid);
		buildPageInfo(book, guid);
		buildTableOfContent(book, guid);

		return book;
	}

	private static BookInfo buildBookInfo(String guid) throws IOException {
		BookInfo book = new BookInfo();
		book.setGuid(guid);
		Olef olef = getOlef(guid);
		book.setOlef(olef);
		book.setTitle(olef.getTitle());
		book.setUrl(DOMAIN_NAME + "/portal/bhle-view/bhle:" + GUID_BANK_ID
				+ "-" + guid);
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
			page.setType(olef.getPageType(i));
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
			int entryPageIndex = Integer.valueOf(olef.getEntryPage()) - 1;
			book.setEntryPageIndex(entryPageIndex);
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
		int[] dimensions = ImageUtil.tiffToJp2Size(pageFile.getAbsolutePath());
		page.setHeight(dimensions[0]);
		page.setWidth(dimensions[1]);
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

	private static Olef getOlef(String guid) throws IOException {
		FedoraURI olefUri = FedoraURI.getFedoraUri(guid, "OLEF");
		URI olefHttpUri = StaticURI.toStaticFileUri(olefUri);
		return new Olef(olefHttpUri.toURL());
	}

	public static void save(BookInfo bookInfo) {
		Derivative derivative = new Derivative();
		derivative.setPid(FedoraURI.getPidFromGuid(bookInfo.getGuid()));
		derivative.setDsId(DSID);
		JSONObject json = JSONObject.fromObject(bookInfo);
		InputStream in = IOUtils.toInputStream(json.toString());
		derivative.setInputStream(in);
		storageService.updateDerivative(derivative);
	}

	public static BookInfo read(String guid) {
		try {
			InputStream in = storageService.openDatastream(guid, DSID, null);
			String bookInfoJson = IOUtils.toString(in);
			return (BookInfo) JSONObject.toBean(
					JSONObject.fromObject(bookInfoJson), BookInfo.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean exists(String guid) {
		try {
			List<URI> uris = storageService.getDatastream(guid, DSID);
			return uris.size() > 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
