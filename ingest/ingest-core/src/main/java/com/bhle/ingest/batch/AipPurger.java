package com.bhle.ingest.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourmediashelf.fedora.client.FedoraClient;

@Component
public class AipPurger implements ItemWriter<String> {

	private static final Logger logger = LoggerFactory
			.getLogger(AipPurger.class);
	
	@Autowired
	private FedoraClient client;

	@Override
	public void write(List<? extends String> pids) throws Exception {
		for (String pid : pids) {
			logger.info("Purge Object: " + pid);
			FedoraClient.purgeObject(pid).execute(client);
		}
	}

}
