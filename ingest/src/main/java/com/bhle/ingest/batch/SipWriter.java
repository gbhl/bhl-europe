package com.bhle.ingest.batch;

import java.io.File;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bhle.ingest.IngestService;

@Component
public class SipWriter implements ItemWriter<File> {

	@Autowired
	private IngestService ingestor;
	
	@Override
	public void write(List<? extends File> files) throws Exception {
		for (File file : files) {
			ingestor.ingestItem(file);
		}
	}

}
