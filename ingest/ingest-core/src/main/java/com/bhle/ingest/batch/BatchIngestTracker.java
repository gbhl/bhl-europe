package com.bhle.ingest.batch;

import java.util.List;

public interface BatchIngestTracker {
	public void init();
	public void addPid(String pid);
	public List<String> getPids();
}
