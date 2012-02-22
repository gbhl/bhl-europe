package com.bhle.access.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.access.bookreader.BookInfo;
import com.bhle.access.bookreader.BookInfoBuilder;
import com.bhle.access.bookreader.search.SearchService;
import com.sun.jersey.api.view.Viewable;

@Component
@Scope("request")
@Path("stream")
public class Stream {

	@GET
	@Path("{guid}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getBookInfo(@PathParam("guid") String guid,
			@DefaultValue("false") @QueryParam("refresh") boolean refresh) {
		BookInfo bookInfo = null;

		if (!refresh && BookInfoBuilder.exists(guid)) {
			bookInfo = BookInfoBuilder.read(guid);
		} else {
			bookInfo = BookInfoBuilder.build(guid);
			BookInfoBuilder.save(bookInfo);
		}

		JSONObject json = JSONObject.fromObject(bookInfo);

		return new Viewable("/bookreader", json.toString());
	}
	
	@GET
	@Path("search/{guid}")
	@Produces("application/json")
	public String search(@PathParam("guid") String guid,
			@QueryParam("query") String query) {
		return SearchService.query(guid, query);
	}
}
