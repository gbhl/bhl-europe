package com.bhle.ingest;

import java.io.File;

public interface IngestService {
	public void ingestPackage(Sip sip);
	public int ingestItem(File file);
}
