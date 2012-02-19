package com.bhle.access.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.csvreader.CsvReader;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;
import com.yourmediashelf.fedora.client.response.GetDatastreamResponse;
import com.yourmediashelf.fedora.client.response.GetObjectProfileResponse;
import com.yourmediashelf.fedora.client.response.ListDatastreamsResponse;
import com.yourmediashelf.fedora.client.response.RiSearchResponse;
import com.yourmediashelf.fedora.generated.access.DatastreamType;

@Component
public class FedoraUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(FedoraUtil.class);

	public static final String PAGE_MODEL = "bhle-cmodel:pageCModel";

	private static FedoraClient client;

	@Autowired
	public void setClient(FedoraCredentials credentials) {
		FedoraUtil.client = new FedoraClient(credentials);
	}

	public static List<String> getAllObjectsPids() {
		try {
			FindObjectsResponse findObjectsResponse = FedoraClient
					.findObjects().terms("*").pid().execute(client);
			return findObjectsResponse.getPids();
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getContentModels(String pid) {
		try {
			GetObjectProfileResponse getObjectProfileResponse = FedoraClient
					.getObjectProfile(pid).execute(client);
			return getObjectProfileResponse.getObjectProfile().getObjModels()
					.getModel();
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<DatastreamType> listDatastreams(String pid) {
		try {
			ListDatastreamsResponse listDatastreamsResponse = FedoraClient
					.listDatastreams(pid).execute(client);
			return listDatastreamsResponse.getDatastreams();
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDatastreamMIME(String pid, String dsid) {
		try {
			GetDatastreamResponse getDatastreamResponse = FedoraClient
					.getDatastream(pid, dsid).execute(client);
			return getDatastreamResponse.getDatastreamProfile().getDsMIME();
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getDatastreamDissemination(String pid, String dsid) {
		try {
			FedoraResponse response = FedoraClient.getDatastreamDissemination(
					pid, dsid).execute(client);
			return response.getEntityInputStream();
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[] getAllMembers(String pid, String contentModel) {
		String query = "select $object from <#ri> "
				+ "where ($object <fedora-rels-ext:isMemberOf> <fedora:" + pid
				+ "> " + "and $object <fedora-model:hasModel> <info:fedora/"
				+ contentModel + ">)";
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

	public static void ingestFOXML(File file) {
		try {
			FedoraClient.ingest().content(file).execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
	}

	public static void purgeObject(String pid) {
		try {
			FedoraClient.purgeObject(pid).execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
	}
}
