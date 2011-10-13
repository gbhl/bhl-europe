package util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class ResourceIndex {

	public static int getPageCount(String pid) {

		int pageCount = 0;

		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(GlobalParameter.BASE_URL + "/"
				+ GlobalParameter.FEDORA + "/" + GlobalParameter.RESOURCE_INDEX);
		String query = "select $object from <#ri> "
				+ "where ($object <fedora-model:hasModel> <fedora:ilives:pageCModel> "
				+ "and $object <fedora-rels-ext:isMemberOf> <fedora:" + pid
				+ ">)";
		NameValuePair[] data = { new NameValuePair("type", "tuples"),
				new NameValuePair("format", "count"),
				new NameValuePair("lang", "iTQL"),
				new NameValuePair("query", query) };
		post.setRequestBody(data);
		try {
			client.executeMethod(post);
			String pageCountString = post.getResponseBodyAsString();
			pageCount = Integer.valueOf(pageCountString);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pageCount;
	}
}
