package com.atos.bookingester.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.atos.bookingester.util.MyHttpClient;

public class RemoteIngestQueue implements IngestQueue {
	private static String retriveAccess = "http://bhl-mandible.nhm.ac.uk:8100/PreIngest/archivingqueue/retrieve";
	private static String resetAccess = "http://bhl-mandible.nhm.ac.uk:8100/PreIngest/archivingqueue/reset";

	private List<InformationPackage> packs = new ArrayList<InformationPackage>();

	@Override
	public void query() {
		String resp = MyHttpClient.get(retriveAccess);
		JSONArray jsonArray = JSONArray.fromObject(resp);
		if (!jsonArray.isEmpty()) {
			for (Object packObject : jsonArray) {
				InformationPackage pack = (InformationPackage) JSONObject
						.toBean((JSONObject) packObject,
								InformationPackage.class);
				pack.setType(InformationPackageType.URL);
				packs.add(pack);
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return packs.isEmpty();
	}

	@Override
	public List<InformationPackage> getInformationPackages() {
		return packs;
	}

	@Override
	public void reset() {
		String resp = MyHttpClient.get(resetAccess);
		
		packs.clear();
	}
}