package com.bhle.ingest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.response.FindObjectsResponse;

public class QueryAndPurge {

	/**
	 * @param args
	 * @throws FedoraClientException
	 */
	public static void main(String[] args) throws FedoraClientException {
		if (args.length != 1) {
			System.err.println("Query not found!");
			return;
		}

		String query = args[0];

		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "/META-INF/spring/env-context.xml",
						"/META-INF/spring/commons-context.xml" });
		FedoraClient client = (FedoraClient) applicationContext
				.getBean("fedoraClient");

		FindObjectsResponse findObjectsResponse = FedoraClient.findObjects()
				.terms(query).maxResults(Integer.MAX_VALUE).pid()
				.execute(client);

		List<String> pids = findObjectsResponse.getPids();

		System.out.println("Objects (count: " + pids.size() + "):");
		for (String pid : pids) {
			System.out.println(pid);
		}
		System.out.print("Purge the objects above? [Y/N]");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String response = null;
		try {
			response = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response.endsWith("Y")) {
			for (String pid : pids) {
				System.out.println("Purge Object: " + pid);
				FedoraClient.purgeObject(pid).execute(client);
			}
		}
	}

}
