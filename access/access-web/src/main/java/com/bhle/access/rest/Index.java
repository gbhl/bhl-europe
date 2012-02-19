package com.bhle.access.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.view.Viewable;

@Path("/")
public class Index {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getIndex(){
		return new Viewable("/index");
	}
	
}
