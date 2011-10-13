package com.atosorigin.bhle;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.akubraproject.BlobStore;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.mux.AbstractMuxConnection;

public class BHLMuxConnection extends AbstractMuxConnection {
	private final BlobStore longTermStore;
	private final BlobStore shortTermStore;
	private final String[] dataStreamIdFilters;

	public BHLMuxConnection(BlobStore store, BlobStore longTermStore,
			BlobStore shortTermStore,
			String[] dataStreamIdFilters) {
		super(store, null);
		this.longTermStore = longTermStore;
		this.shortTermStore = shortTermStore;
		this.dataStreamIdFilters = dataStreamIdFilters;
	}

	@Override
	public BlobStore getStore(URI blobId, Map<String, String> hints)
			throws IOException, UnsupportedIdException {
		String dataStramId = extractDataStramId(blobId);
		if(containsDataStreamIdFilter(dataStramId)){
			return longTermStore;
		} else {
			return shortTermStore;
		}
	}

	private boolean containsDataStreamIdFilter(String dataStramId) {
		for (String filter : dataStreamIdFilters){
			if (dataStramId.equals(filter)){
				return true;
			}
		}
		return false;
	}

	private String extractDataStramId(URI blobId) {
		String[] parts = blobId.getSchemeSpecificPart().split("/");
		return parts[parts.length - 2];
	}

}
