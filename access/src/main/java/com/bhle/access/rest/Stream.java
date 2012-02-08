package com.bhle.access.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.access.bookreader.BookInfo;
import com.bhle.access.bookreader.BookInfoBuilder;
import com.bhle.access.domain.Derivative;
import com.bhle.access.storage.StorageService;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.FedoraUtil;
import com.sun.jersey.api.view.Viewable;

@Component
@Scope("request")
@Path("stream")
public class Stream {

	@Autowired
	private StorageService service;

	private static String DSID = "BOOKREADER";

	@GET
	@Path("{guid}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getBookInfo(@PathParam("guid") String guid,
			@DefaultValue("false") @QueryParam("refresh") boolean refresh) {
		BookInfo bookInfo = null;

		if (!refresh && isBookInfoAvailable(guid)) {
			bookInfo = readPregeneratedBookInfo(guid);
		} else {
			bookInfo = BookInfoBuilder.build(guid);
			storeBoonInfo(guid, bookInfo);
		}

		JSONObject json = JSONObject.fromObject(bookInfo);

		return new Viewable("/bookreader", json.toString());
	}

	private BookInfo readPregeneratedBookInfo(String guid) {
		try {
			InputStream in = service.openDatastream(guid, DSID, null);
			String bookInfoJson = IOUtils.toString(in);
			return (BookInfo) JSONObject.toBean(
					JSONObject.fromObject(bookInfoJson), BookInfo.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void storeBoonInfo(String guid, BookInfo bookInfo) {
		Derivative derivative = new Derivative();
		derivative.setPid(FedoraURI.getPidFromGuid(guid));
		derivative.setDsId(DSID);
		JSONObject json = JSONObject.fromObject(bookInfo);
		derivative.setInputStream(IOUtils.toInputStream(json.toString()));
		service.updateDerivative(derivative);

	}

	private boolean isBookInfoAvailable(String guid) {
		try {
			List<URI> uris = service.getDatastream(guid, DSID);
			return uris.size() > 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
