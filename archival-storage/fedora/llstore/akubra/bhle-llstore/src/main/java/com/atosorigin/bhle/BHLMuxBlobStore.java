package com.atosorigin.bhle;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.transaction.Transaction;

import org.akubraproject.BlobStore;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.impl.AbstractBlobStore;
import org.akubraproject.mux.AbstractMuxStore;

public class BHLMuxBlobStore extends AbstractMuxStore {
	private final BlobStore longTermStore;
	private final BlobStore shortTermStore;
	private final String[] dataStreamIdFilters;

	protected BHLMuxBlobStore(URI id, BlobStore longTermStore,
			BlobStore shortTermStore, String... dataStreamIdFilters) {
		super(id);
		this.longTermStore = longTermStore;
		this.shortTermStore = shortTermStore;
		this.dataStreamIdFilters = dataStreamIdFilters;
	}

	public BlobStoreConnection openConnection(Transaction tx,
			Map<String, String> hints) throws UnsupportedOperationException,
			IOException {
		return new BHLMuxConnection(this, longTermStore, shortTermStore, dataStreamIdFilters);
	}
}
