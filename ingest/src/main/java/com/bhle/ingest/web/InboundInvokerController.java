package com.bhle.ingest.web;

import java.net.URI;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.bhle.ingest.IngestService;
import com.bhle.ingest.Sip;

@Controller
public class InboundInvokerController {
	
	@Autowired
	private IngestService service;
	
//	@RequestMapping(value = "/", method = RequestMethod.POST)
//	@ResponseStatus(HttpStatus.CREATED)
	public void test(@RequestParam(value = "uri") URI uri) {
//		service.ingestPackage(new Sip(uri));
	}
}
