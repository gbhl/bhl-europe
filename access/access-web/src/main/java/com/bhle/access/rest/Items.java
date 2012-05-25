package com.bhle.access.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.access.convert.ConverterManager;
import com.bhle.access.feed.AtomFactory;
import com.bhle.access.storage.StorageService;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.FedoraUtil;
import com.sun.jersey.api.NotFoundException;

@Component
@Scope("request")
@Path("items")
public class Items {
	@Context
	UriInfo uriInfo;

	@Autowired
	private StorageService service;

	private static final Logger logger = LoggerFactory.getLogger(Items.class);

	@GET
	public Response getItems() {
		try {
			List<URI> guids = service.listGuids();
			return Response.ok(AtomFactory.buildAtom(guids))
					.type(MediaType.APPLICATION_ATOM_XML).build();
		} catch (IOException e) {
			e.printStackTrace();
			throw new NotFoundException();
		}
	}

	@GET
	@Path("{guid}")
	public Response getItem(@PathParam("guid") String guid) {
		try {
			List<URI> dsids = service.listDatastreams(guid);
			return Response.ok(AtomFactory.buildAtom(dsids))
					.type(MediaType.APPLICATION_ATOM_XML).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotFoundException();
		}
	}

	@GET
	@Path("{guid}/{dsid}")
	public Response getDatastream(@PathParam("guid") String guid,
			@PathParam("dsid") String dsid) {
		List<URI> dsids = null;
		try {
			dsids = service.getDatastream(guid, dsid);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotFoundException();
		}
		if (dsids.isEmpty()) {
			throw new NotFoundException("Datastream " + dsid + " of GUID "
					+ guid + " is not found");
		} else if (dsids.size() == 1) {
			try {
				InputStream in = service.openDatastream(guid, dsid, null);
				return Response.ok(in).type(ConverterManager.getMimeType(dsid))
						.build();
			} catch (Exception e) {
				e.printStackTrace();
				throw new NotFoundException();
			}
		} else {
			return Response.ok(AtomFactory.buildAtom(dsids))
					.type(MediaType.APPLICATION_ATOM_XML).build();
		}
	}

	@GET
	@Path("{guid}/{dsid}/{serialNumber:\\d+}")
	public Response getDatastreamBySerialNumber(@PathParam("guid") String guid,
			@PathParam("dsid") String dsid,
			@PathParam("serialNumber") String serialNumber) {
		InputStream in;
		try {
			in = service.openDatastream(guid, dsid, serialNumber);
			return Response.ok(in).type(ConverterManager.getMimeType(dsid))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotFoundException();
		}
	}
	
	@GET
	@Path("{guid}/members")
	public Response getMembers(@PathParam("guid") String guid){
		String result = FedoraUtil.getAllMembersExceptPage(FedoraURI.getPidFromGuid(guid));
		return Response.ok(result).build();
	}
	
	@GET
	@Path("{guid}/parent")
	public Response getParent(@PathParam("guid") String guid){
		String result = FedoraUtil.getParent(FedoraURI.getPidFromGuid(guid));
		return Response.ok(result).build();
	}
}
