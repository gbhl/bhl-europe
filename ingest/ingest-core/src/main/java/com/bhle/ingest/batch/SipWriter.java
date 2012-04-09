package com.bhle.ingest.batch;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import com.bhle.ingest.FedoraService;

public class SipWriter implements ItemWriter<File> {

	private static final Logger logger = LoggerFactory
			.getLogger(SipWriter.class);
	
	private FedoraService ingestor;
	
	public void setIngestor(FedoraService ingestor) {
		this.ingestor = ingestor;
	}
	
	
	@Override
	public void write(List<? extends File> files) throws Exception {
		for (File file : files) {
			logger.info("Ingest File: " + file.getName());
			ingestor.ingestItem(file);
		}
	}

}
