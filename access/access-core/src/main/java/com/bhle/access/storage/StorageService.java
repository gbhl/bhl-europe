package com.bhle.access.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.domain.Derivative;
import com.bhle.access.util.FedoraURI;

public class StorageService {
	private static final Logger logger = LoggerFactory
			.getLogger(StorageService.class);

	private LowLevelStorage defaultStorages;

	public void setDefaultStorages(LowLevelStorage defaultStorages) {
		this.defaultStorages = defaultStorages;
	}

	public List<URI> listGuids() throws IOException {
		List<URI> guidUris = new ArrayList<URI>();
		Set<String> guidSet = new HashSet<String>();

		List<URI> uris = defaultStorages.list(null);
		for (URI uri : uris) {
			FedoraURI fedoraUri = new FedoraURI(uri);
			if (!guidSet.contains(fedoraUri.getGuid())) {
				guidSet.add(fedoraUri.getGuid());
				guidUris.add(fedoraUri.getItemURI());
			}
		}

		return guidUris;
	}

	public List<URI> listDatastreams(String guid) throws IOException {
		List<URI> dsidUris = new ArrayList<URI>();
		List<URI> uris = defaultStorages.list(guid);
		for (URI uri : uris) {
			dsidUris.add(uri);
		}

		return dsidUris;
	}

	public List<URI> getDatastream(String guid, String dsid) throws IOException {
		List<URI> result = new ArrayList<URI>();

		List<URI> dsidUris = listDatastreams(guid);
		for (URI dsidUri : dsidUris) {
			FedoraURI fedoraUri = new FedoraURI(dsidUri);
			if (dsid.equalsIgnoreCase(fedoraUri.getDsid())) {
				result.add(dsidUri);
			}
		}
		return result;
	}

	public InputStream openDatastream(String guid, String dsid,
			String serialNumber) throws IOException {
		List<URI> uris = getDatastream(guid, dsid);

		if (uris.size() == 0) {
			throw new IOException("Datastream " + guid + " " + dsid
					+ " not found");
		}

		if (serialNumber == null && uris.size() > 1) {
			throw new IOException();
		} else {
			FedoraURI fedoraURI = new FedoraURI(uris.get(0));
			return defaultStorages.get(fedoraURI.getPid(), fedoraURI.getDsid());
		}
	}

	public void deleteObject(String pid) {
		try {
			defaultStorages.remove(pid, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateDerivative(Derivative derivative) {
		logger.debug("Update Derivative: " + derivative.getPid() + " "
				+ derivative.getDsId());

		try {
			defaultStorages.replace(derivative.getPid(), derivative.getDsId(),
					derivative.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteDerivative(Derivative derivative) {
		logger.debug("Update Derivative: " + derivative.getPid() + " "
				+ derivative.getDsId());
		try {
			defaultStorages.remove(derivative.getPid(), derivative.getDsId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
