package com.bhle.access.bookreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.domain.Derivative;
import com.bhle.access.storage.StorageService;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.ImageUtil;
import com.bhle.access.util.Olef;
import com.bhle.access.util.StaticURI;

public class ThumbnailBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(ThumbnailBuilder.class);

	private static StorageService storageService;

	public void setStorageService(StorageService storageService) {
		ThumbnailBuilder.storageService = storageService;
	}

	private static String DSID = "THUMBNAIL";

	public static InputStream build(String guid) throws IOException {
		logger.debug("Build Thumbnail");

		Olef olef = getOlef(guid);

		String entryPage = olef.getEntryPage();
		if (entryPage.equals("")) {
			entryPage = "1";
		}
		int entryPageSequence = Integer.valueOf(entryPage);
		FedoraURI entryPageUri = FedoraURI.getFedoraUri(guid,
				entryPageSequence, "JP2");
		URI entryPageHttpUri = StaticURI.toStaticFileUri(entryPageUri);
		InputStream jp2InputStream = null;
		jp2InputStream = entryPageHttpUri.toURL().openStream();
		return ImageUtil.jp2ToThumbnail(jp2InputStream);
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

	public static InputStream read(String guid) {
		try {
			return storageService.openDatastream(guid, DSID, null);
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

	public static void save(String guid, InputStream thumbnailInputStream) {
		Derivative derivative = new Derivative();
		derivative.setPid(FedoraURI.getPidFromGuid(guid));
		derivative.setDsId(DSID);
		derivative.setInputStream(thumbnailInputStream);
		storageService.updateDerivative(derivative);
	}
}
