package com.bhle.ingest.batch;

import java.util.List;

public interface BatchIngestTracker {
	public void init(String guid);
	public void addMember(String guid, String memberId);
	public List<String> getMembers(String guid);
	public void remove(String guid);
}
