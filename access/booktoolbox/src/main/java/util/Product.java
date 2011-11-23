package util;

import java.util.Locale;

import javax.ws.rs.core.StreamingOutput;

public class Product {
	private String name;
	private boolean offline;
	private String email;
	private Format format;
	private StreamingOutput outputStream;

	private Locale lang;

	private Product(String name, boolean offline, String email, Locale lang) {
		this.name = name;
		this.offline = offline;
		this.email = email;
		if (lang != null) {
			this.lang = lang;
		} else {
			this.lang = Locale.ENGLISH;
		}
	}

	public static Product newRealtimeProduct(String name, Locale lang) {
		return new Product(name, false, null, lang);
	}

	public static Product newOfflineProduct(String name, String email, Locale lang) {
		return new Product(name, true, email, lang);
	}

	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public StreamingOutput getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(StreamingOutput outputStream) {
		this.outputStream = outputStream;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Locale getLang() {
		return lang;
	}
}
