package com.bhle.ingest.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BatchIngestTrackerImpl implements BatchIngestTracker {

	private Map<String, List<String>> map = new ConcurrentHashMap<String, List<String>>();

	@Override
	public void init(String guid) {
		List<String> members = map.get(guid);
		if (members == null) {
			members = new ArrayList<String>();
			map.put(guid, members);
		}
	}

	@Override
	public void addMember(String guid, String memberId) {
		List<String> members = map.get(guid);
		members.add(memberId);
	}

	@Override
	public List<String> getMembers(String guid) {
		return map.get(guid);
	}

	@Override
	public void remove(String guid) {
		map.remove(guid);
	}

}
