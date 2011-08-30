package com.atos.bookingester.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

public class MyHttpClient {
	public static HttpClient newInstance(){
		HttpClient client = new HttpClient();
		//client.getHostConfiguration().setProxy("proxy2.si.fr.atosorigin.com", 3128);
		return client;
	}
	
	public static String get(String url){
		return getAsString(url);
	}
	
	public static String getAsString(String url){
		String resp = null;
		
		HttpClient client = newInstance();
		HttpMethod method = new GetMethod(url);
		try {
			client.executeMethod(method);
			resp = method.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resp;
	}
	
	public static InputStream getAsStream(String url) throws IOException{
		HttpClient client = newInstance();
		HttpMethod method = new GetMethod(url);
		try {
			client.executeMethod(method);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return method.getResponseBodyAsStream();
	}
}
