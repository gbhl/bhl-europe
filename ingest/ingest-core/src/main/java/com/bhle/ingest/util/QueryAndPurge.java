package com.bhle.ingest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
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

		List<String> pids = new LinkedList<String>();
		String token = null;
		
		do {
			FindObjectsResponse findObjectsResponse = null;
			try {
				findObjectsResponse = FedoraClient
				.findObjects().terms("*").pid().sessionToken(token).maxResults(Integer.MAX_VALUE).execute(client);
			} catch (FedoraClientException e) {
				e.printStackTrace();
			}
			pids.addAll(findObjectsResponse.getPids());
			token = findObjectsResponse.getToken();
		} while (token != null && !token.equals(""));
		
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
