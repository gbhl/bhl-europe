package com.atos.bookingester.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import com.atos.bookingester.util.MyHttpClient;

public class IngestQueue {
	private static String retriveAccess = "http://bhl-mandible.nhm.ac.uk:8100/PreIngest/archivingqueue/retrieve";
	private static String resetAccess = "http://bhl-mandible.nhm.ac.uk:8100/PreIngest/archivingqueue/reset";

	private static List<InformationPackage> packs = new ArrayList<InformationPackage>();

	public static void query() {
		packs.clear();

		String resp = MyHttpClient.get(retriveAccess);
		JSONArray jsonArray = JSONArray.fromObject(resp);
		if (!jsonArray.isEmpty()) {
			for (Object packObject : jsonArray) {
				InformationPackage pack = (InformationPackage) JSONObject
						.toBean((JSONObject) packObject,
								InformationPackage.class);
				packs.add(pack);
			}
		}
	}

	public static boolean isEmpty() {
		return packs.isEmpty();
	}

	public static List<InformationPackage> getInformationPackages() {
		return packs;
	}

	public static void reset() {
		String resp = MyHttpClient.get(resetAccess);
	}
}