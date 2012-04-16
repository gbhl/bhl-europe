package com.bhle.ingest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;

import com.bhle.ingest.batch.IngestException;
import com.bhle.ingest.util.CsvUtil;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import com.yourmediashelf.fedora.client.response.RiSearchResponse;

public class FedoraServiceImpl {

	private static final Logger logger = LoggerFactory
			.getLogger(FedoraServiceImpl.class);

	private FedoraClient client;

	public FedoraServiceImpl(FedoraClient client) {
		this.client = client;
	}

	@ServiceActivator
	public int ingestMETS(Message<File> message) throws IngestException {
		IngestResponse response = null;
		try {
			response = FedoraClient.ingest().content(message.getPayload())
					.format("info:fedora/fedora-system:METSFedoraExt-1.1")
					.execute(client);
		} catch (FedoraClientException e) {
			throw new IngestException(e);
		}
		return response.getStatus();
	}

	public int purge(String pid) {
		FedoraResponse response = null;
		try {
			logger.info("Purge Object: " + pid);
			response = FedoraClient.purgeObject(pid).execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		if (response != null) {
			return response.getStatus();
		} else {
			return -1;
		}

	}

	public void purgeAllMembers(String pid) {
		String query = "select $object from <#ri> "
				+ "where ($object <fedora-rels-ext:isMemberOf> <fedora:" + pid
				+ "> " + "or $object <dc:identifier> " + "'" + pid + "')";

		logger.debug(query);

		try {
			RiSearchResponse riSearchResponse = FedoraClient.riSearch(query)
					.lang("itql").format("csv").type("tuples").execute(client);
			String csv = IOUtils.toString(riSearchResponse
					.getEntityInputStream());
			String[] pids = CsvUtil.readOneColumnCsv(csv);
			for (String memberPid : pids) {
				purge(memberPid.split("/")[1]);
			}
		} catch (FedoraClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void activate(String pid) {
		try {
			FedoraClient.modifyObject(pid).state("A").execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
	}
}
