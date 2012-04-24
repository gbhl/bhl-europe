package com.bhle.ingest.batch;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchIngestTrackerImpl implements BatchIngestTracker {

	private static final Logger logger = LoggerFactory
			.getLogger(BatchIngestTrackerImpl.class);
	
	private List<String> pids;

	@Override
	public void addPid(String pid) {
		logger.debug("Add {} to tracker", pid);
		pids.add(pid);
	}

	@Override
	public List<String> getPids() {
		return pids;
	}

	@Override
	public void init() {
		pids = new ArrayList<String>();
	}

}
