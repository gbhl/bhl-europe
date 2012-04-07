package com.bhle.ingest.batch;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.ingest.FedoraService;

@Component
@Scope("step")
public class SipWriter implements ItemWriter<File> {

	private static final Logger logger = LoggerFactory
			.getLogger(SipWriter.class);
	
	@Autowired
	private FedoraService ingestor;
	
	
	@Override
	public void write(List<? extends File> files) throws Exception {
		for (File file : files) {
			logger.info("Ingest File: " + file.getName());
			ingestor.ingestItem(file);
		}
	}

}
