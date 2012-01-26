package com.bhle.access.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

import com.bhle.access.download.Resolution;
import com.bhle.access.download.offline.OfflineProcessingService;
import com.bhle.access.download.offline.RawRequest;
import com.sun.jersey.api.view.Viewable;

@Path("offline")
public class Offline {

	private static final Logger logger = LoggerFactory.getLogger(Offline.class);

	@Autowired
	private OfflineProcessingService offlineService;

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response receiveOfflineRequest(@FormParam("email") String email,
			@FormParam("format") String format, @FormParam("guid") String guid,
			@FormParam("ranges") String ranges,
			@FormParam("resolution") String resolution) {
		logger.info("Receive Request from: " + email);

		RawRequest rawRequest = new RawRequest();
		rawRequest.setEmail(email);
		rawRequest.setGuid(guid);
		rawRequest.setFormat(format);
		rawRequest.setRanges(ranges);
		rawRequest.setResolution(resolution);

		offlineService.handleRawRequest(rawRequest);
		return Response.ok().build();
	}
}
