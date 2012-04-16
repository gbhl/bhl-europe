package com.bhle.ingest.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.yourmediashelf.fedora.client.FedoraClient;

public class AipPurger implements ItemWriter<String> {

	private FedoraClient client;
	
	public void setClient(FedoraClient client) {
		this.client = client;
	}

	@Override
	public void write(List<? extends String> pids) throws Exception {
		for (String pid : pids) {
			FedoraClient.purgeObject(pid).execute(client);
		}
	}

}
