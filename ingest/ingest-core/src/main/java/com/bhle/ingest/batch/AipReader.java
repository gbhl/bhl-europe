package com.bhle.ingest.batch;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.bhle.ingest.util.CsvUtil;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.response.RiSearchResponse;

public class AipReader implements ItemReader<String> {

	private String[] pids;
	private int index = 0;

	private FedoraClient client;

	public void setClient(FedoraClient client) {
		this.client = client;
	}

	private String guid;

	public void setGuid(String guid) {
		this.guid = guid.replace("/", "-");
	}

	@BeforeStep
	public void init(StepExecution stepExecution) {
		// this.stepExecution = stepExecution;
		pids = getAllMemberOfGuid(guid);
	}

	@Override
	public String read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		if (pids != null && index <= pids.length - 1) {
			String pid = pids[index].split("/")[1];
			index++;
			return pid;
		}
		return null;

	}

	private String[] getAllMemberOfGuid(String guid) {
		String query = "select $object from <#ri> "
				+ "where ($object <fedora-rels-ext:isMemberOf> <fedora:" + guid
				+ "> " + "or $object <dc:identifier> " + "'" + guid + "')";
		try {
			RiSearchResponse riSearchResponse = FedoraClient.riSearch(query)
					.lang("itql").format("csv").type("tuples").execute(client);
			String csv = IOUtils.toString(riSearchResponse
					.getEntityInputStream());
			return CsvUtil.readOneColumnCsv(csv);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
