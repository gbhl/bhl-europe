package com.bhle.access.download;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.jasypt.util.text.TextEncryptor;

public class DownloadLocationHelper {

	private static TextEncryptor encryptor;
	
	public DownloadLocationHelper(TextEncryptor encryptor) {
		DownloadLocationHelper.encryptor = encryptor;
	}

	public static String encrypt(String blobIdPath) {
		String encryptedPath = encryptor.encrypt(blobIdPath);
		return urlEncode(encryptedPath);
	}

	public static String decrypt(String encodedLocation) {
//		String decodedPath =  urlDecode(encodedLocation);
		return encryptor.decrypt(encodedLocation);
	}

	private static String urlEncode(String path) {
		try {
			return URLEncoder.encode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String urlDecode(String path) {
		try {
			return URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
