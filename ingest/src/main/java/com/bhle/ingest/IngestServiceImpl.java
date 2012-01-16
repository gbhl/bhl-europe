package com.bhle.ingest;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.response.FedoraResponse;

public class IngestServiceImpl {

	private static final Logger logger = LoggerFactory
			.getLogger(IngestServiceTest.class);

	private FedoraClient client;

	public IngestServiceImpl(FedoraClient client) {
		this.client = client;
	}

	@ServiceActivator
	public int ingestMETS(Message<File> message) {
		FedoraResponse response = null;;
		try {
			response = FedoraClient.ingest()
					.content(message.getPayload())
					.format("info:fedora/fedora-system:METSFedoraExt-1.1")
					.execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		return response.getStatus();
	}

	public int purge(String pid) {
		FedoraResponse response = null;
		try {
			response = FedoraClient.purgeObject(pid).execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		return response.getStatus();
	}
}