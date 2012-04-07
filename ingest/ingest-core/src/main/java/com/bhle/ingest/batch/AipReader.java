package com.bhle.ingest.batch;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.ingest.util.CsvUtil;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.response.RiSearchResponse;

@Component
@Scope("step")
public class AipReader implements ItemReader<String> {

	private String[] pids;

	@Autowired
	private FedoraClient client;

	private static final String GUID = "GUID";

	private StepExecution stepExecution;

	private int index;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public String read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {

		if (pids == null) {
			String guid = stepExecution.getJobParameters().getString(GUID);
			pids = getAllMemberOfGuid(guid);
			index = 0;
		}

		if (index > pids.length - 1) {
			return null;
		}

		String pid = pids[index].split("/")[1];
		index++;
		return pid;
	}

	private String[] getAllMemberOfGuid(String guid) {
		String query = "select $object from <#ri> "
				+ "where ($object <fedora-rels-ext:isMemberOf> <fedora:bhle:"
				+ guid + "> " + "or $object <dc:identifier> " + "'bhle:" + guid
				+ "')";
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
