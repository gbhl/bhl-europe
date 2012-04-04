package com.bhle.ingest;

import java.io.File;

import com.bhle.ingest.batch.IngestException;

public interface FedoraService {
	public int ingestItem(File file) throws IngestException;
	
	public int activateItem(String guid);
}
