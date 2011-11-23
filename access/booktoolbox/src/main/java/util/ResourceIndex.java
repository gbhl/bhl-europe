package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class ResourceIndex {

	public static int getPageCount(String pid) {

		int pageCount = 0;

		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(GlobalParameter.URL_RISEARCH);
		String query = "select $object from <#ri> "
				+ "where ($object <fedora-model:hasModel> <fedora:"
				+ GlobalParameter.PID_PAGE_CMODEL + "> "
				+ "and $object <fedora-rels-ext:isMemberOf> <fedora:" + pid
				+ ">)";
		NameValuePair[] data = { new NameValuePair("type", "tuples"),
				new NameValuePair("format", "count"),
				new NameValuePair("lang", "iTQL"),
				new NameValuePair("query", query) };
		post.setRequestBody(data);
		try {
			client.executeMethod(post);

			InputStream inputStream = post.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuffer pageCountString = new StringBuffer();
			String str = "";
			while ((str = br.readLine()) != null) {
				pageCountString.append(str);
			}

			pageCount = Integer.valueOf(pageCountString.toString());
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pageCount;
	}

	public static List<String> getModels(String pid) {
		List<String> modelList = new ArrayList<String>();

		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(GlobalParameter.URL_RISEARCH);
		String query = "select $model from <#ri> " + "where <info:fedora/"
				+ pid + "> <fedora-model:hasModel> $model";

		NameValuePair[] data = { new NameValuePair("type", "tuples"),
				new NameValuePair("format", "csv"),
				new NameValuePair("lang", "iTQL"),
				new NameValuePair("query", query) };
		post.setRequestBody(data);
		try {
			client.executeMethod(post);

			InputStream inputStream = post.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));
			String str = "";
			int line = 0;
			while ((str = br.readLine()) != null) {
				if (line != 0) {
					modelList.add(str.split("/")[1]);
				}
				line++;
			}

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return modelList;
	}
}
