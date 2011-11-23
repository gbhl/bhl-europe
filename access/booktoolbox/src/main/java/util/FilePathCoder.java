package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.sun.jersey.core.util.Base64;

public class FilePathCoder {
	public static String encode(String filePath) {
		String encoded = new String(Base64.encode(filePath));

		String encodedUrl = null;
		try {
			encodedUrl = URLEncoder.encode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedUrl;
	}

	public static String decode(String filePath) {
		String decodedUrl = null;
		try {
			decodedUrl = URLDecoder.decode(filePath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return new String(Base64.decode(decodedUrl));
	}
	
	public static String getFolder(Product product){
		String folder = "temp";
		try {
			folder = URLEncoder.encode(product.getEmail(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return folder;
	}
}
