package com.bhle.access.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import com.bhle.access.bookreader.BookInfo;
import com.bhle.access.bookreader.BookInfoBuilder;
import com.sun.jersey.api.view.Viewable;

@Path("stream")
public class Stream {
	@GET
	@Path("{guid:\\w{8}}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getBookInfo(@PathParam("guid") String guid) {
		BookInfo bookInfo = BookInfoBuilder.build(guid);
		JSONObject json = JSONObject.fromObject(bookInfo);
		return new Viewable("/bookreader", json.toString());
	}
}
