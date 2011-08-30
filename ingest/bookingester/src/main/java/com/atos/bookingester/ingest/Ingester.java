package com.atos.bookingester.ingest;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atos.bookingester.rebuild.DataStream;
import com.atos.bookingester.rebuild.ExternalRelationship;
import com.atos.bookingester.rebuild.FedoraObject;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.generated.management.Datastream;

public class Ingester {
	private FedoraClient client;

	public Ingester(String url, String username, String password) {
		FedoraCredentials credentials;
		try {
			credentials = new FedoraCredentials(url,
					username, password);
			client = new FedoraClient(credentials);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void ingest(FedoraObject object) {
		if (object != null) {
			try {
				System.out.println(object.getDC().getId());
				ingestObject(object);
				addRelationship(object);
				ingestDatastreams(object);
				for (FedoraObject child : object.getChildren()) {
					ingest(child);
				}
			} catch (FedoraClientException e) {
				e.printStackTrace();
			}
		}

	}

	private void ingestObject(FedoraObject object) throws FedoraClientException {
		FedoraClient.ingest(object.getDC().getId())
				.label(object.getDC().getTitle()).execute(client);

	}

	private void addRelationship(FedoraObject object)
			throws FedoraClientException {
		for (ExternalRelationship rel : object.getRelExts()) {
			FedoraClient.addRelationship(object.getDC().getId())
					.predicate(rel.getPredicate()).object(rel.getSubject())
					.execute(client);
		}
	}

	private void ingestDatastreams(FedoraObject object)
			throws FedoraClientException {
		Map<String, DataStream> DataStreams = object.getDatastreams();
		Set<String> dsIdSet = DataStreams.keySet();
		for (String dsId : dsIdSet) {
			DataStream ds = DataStreams.get(dsId);

			switch (ds.getType()) {
			case XML:
				FedoraClient.addDatastream(object.getDC().getId(), dsId)
						.dsLabel(ds.getLabel()).mimeType(ds.getMimeType())
						.controlGroup("X").content((String) ds.getContent())
						.execute(client);
				break;
			case FILE:
				FedoraClient.addDatastream(object.getDC().getId(), dsId)
						.dsLabel(ds.getLabel()).mimeType(ds.getMimeType())
						.controlGroup("M").content((File) ds.getContent())
						.execute(client);
				break;
			case TEXT:
				FedoraClient.addDatastream(object.getDC().getId(), dsId)
						.dsLabel(ds.getLabel()).mimeType(ds.getMimeType())
						.controlGroup("M").content((String) ds.getContent())
						.execute(client);
				break;
			case URL:
				FedoraClient.addDatastream(object.getDC().getId(), dsId)
						.dsLabel(ds.getLabel()).mimeType(ds.getMimeType())
						.controlGroup("M").dsLocation(ds.getLocation())
						.execute(client);
				break;
			}
		}
	}
}
