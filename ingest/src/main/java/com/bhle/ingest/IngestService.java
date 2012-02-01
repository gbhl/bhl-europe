package com.bhle.ingest;

import java.io.File;

import com.bhle.ingest.batch.IngestException;

public interface IngestService {
	public int ingestItem(File file) throws IngestException;
}
