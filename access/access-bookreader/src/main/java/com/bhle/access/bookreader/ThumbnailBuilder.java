package com.bhle.access.bookreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import com.bhle.access.domain.Derivative;
import com.bhle.access.storage.StorageService;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.ImageUtil;
import com.bhle.access.util.Olef;
import com.bhle.access.util.StaticURI;

public class ThumbnailBuilder {

	private static StorageService storageService;

	public void setStorageService(StorageService storageService) {
		ThumbnailBuilder.storageService = storageService;
	}

	private static String DSID = "THUMBNAIL";

	public static InputStream build(String guid) {
		Olef olef = getOlef(guid);
		String entryPageName = olef.getEntryPage();
		int entryPageIndex = 1;
		if (!entryPageName.equals("")) {
			entryPageIndex = Integer.valueOf(olef.getEntryPage()) - 1;
		}

		FedoraURI entryPageUri = FedoraURI.getFedoraUri(guid, entryPageIndex,
				"JP2");
		URI entryPageHttpUri = StaticURI.toStaticHttpUri(entryPageUri);
		InputStream jp2InputStream = null;
		try {
			jp2InputStream = entryPageHttpUri.toURL().openStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
