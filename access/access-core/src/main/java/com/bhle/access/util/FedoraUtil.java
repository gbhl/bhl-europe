package com.bhle.access.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	public static final String PAGE_MODEL = "bhle-cmodel:pageCModel";

	private static FedoraClient client;

	@Autowired
	public void setClient(FedoraCredentials credentials) {
		FedoraUtil.client = new FedoraClient(credentials);
	}

	public static List<String> getAllObjectsPids() {
		List<String> pids = new LinkedList<String>();
		String token = null;

		do {
			FindObjectsResponse findObjectsResponse = null;
			try {
				findObjectsResponse = FedoraClient.findObjects().terms("*")
						.pid().sessionToken(token)
						.maxResults(Integer.MAX_VALUE).execute(client);
			} catch (FedoraClientException e) {
				e.printStackTrace();
			}
			pids.addAll(findObjectsResponse.getPids());
			token = findObjectsResponse.getToken();
		} while (token != null && !token.equals(""));

		return pids;
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

	public static String[] getAllMembersOfPid(String pid) {
		String query = "select $object from <#ri> "
				+ "where ($object <fedora-rels-ext:isMemberOf> <fedora:" + pid
				+ "> " + "or $object <dc:identifier> " + "'" + pid + "')";

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

	public static String[] getAllInstances(String contentModel) {
		String query = "select $object from <#ri> "
				+ "where $object <fedora-model:hasModel> <info:fedora/"
				+ contentModel + ">";
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

	public static String getAllMembersExceptPage(String pid) {
		String query = "select $object from <#ri> "
				+ "where $object <fedora-rels-ext:isMemberOf> <fedora:"
				+ pid
				+ "> "
				+ "minus $object <fedora-model:hasModel> <info:fedora/bhle-cmodel:pageCModel>";

		try {
			RiSearchResponse riSearchResponse = FedoraClient.riSearch(query)
					.lang("itql").format("csv").type("tuples").execute(client);
			String csv = IOUtils.toString(riSearchResponse
					.getEntityInputStream());
			return csv;
		} catch (FedoraClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getParent(String pid) {
		String query = "select $object from <#ri> " + "where <fedora:" + pid
				+ "> <fedora-rels-ext:isMemberOf> $object";

		try {
			RiSearchResponse riSearchResponse = FedoraClient.riSearch(query)
					.lang("itql").format("csv").type("tuples").execute(client);
			String csv = IOUtils.toString(riSearchResponse
					.getEntityInputStream());
			return csv;
		} catch (FedoraClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[] getAllBookObjects() {
		String query = "select $object from <#ri> "
				+ "where $object <fedora-model:hasModel> <info:fedora/bhle-cmodel:monographCModel> "
				+ "or $object <fedora-model:hasModel> <info:fedora/bhle-cmodel:serialCModel> "
				+ "or $object <fedora-model:hasModel> <info:fedora/bhle-cmodel:sectionCModel> "
				+ "or $object <fedora-model:hasModel> <info:fedora/bhle-cmodel:volumeCModel> "
				+ "or $object <fedora-model:hasModel> <info:fedora/bhle-cmodel:chapterCModel> "
				+ "or $object <fedora-model:hasModel> <info:fedora/bhle-cmodel:articleCModel>";

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

	public static Date getLastModifiedDate(String pid) {
		try {
			GetObjectProfileResponse response = FedoraClient.getObjectProfile(
					pid).execute(client);
			return response.getLastModifiedDate();
		} catch (FedoraClientException e) {
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

	public static void ingestFOXML(InputStream in) {
		try {
			File tmp = File.createTempFile("ingest", null);
			IOUtils.copy(in, FileUtils.openOutputStream(tmp));
			FedoraClient.ingest().content(tmp).execute(client);
			tmp.delete();
		} catch (FedoraClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
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

	public static void activateObject(String pid) {
		try {
			FedoraClient.modifyObject(pid).state("A").execute(client);
		} catch (FedoraClientException e) {
			e.printStackTrace();
		}
	}
}
