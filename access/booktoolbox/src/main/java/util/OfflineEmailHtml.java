package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

public class OfflineEmailHtml {
	private Locale locale;
	private ResourceBundle bundle;

	public OfflineEmailHtml(Locale locale) {
		this.locale = locale;
		this.bundle = ResourceBundle.getBundle("i18n/email", locale);
	}

	public Response askEmail(final String format, final Resolution resolution,
			final List<String> pidList) {
		String html = getHTMLAfterI18n(GlobalParameter.PATH_EMAIL);

		html = html.replaceAll("\\$\\{format\\}", format);
		html = html.replaceAll("\\$\\{resolution\\}",
				resolution.getResolution());
		StringBuffer pidListStringBuffer = new StringBuffer();
		for (String pid : pidList) {
			pidListStringBuffer.append(pid);
			if (pidList.indexOf(pid) != pidList.size() - 1) {
				pidListStringBuffer.append(",");
			}
		}
		html = html.replaceAll("\\$\\{ranges\\}",
				pidListStringBuffer.toString());

		html = html.replaceAll("\\$\\{lang\\}", locale.getLanguage());

		return Response.ok(html).type(MediaType.TEXT_HTML).build();
	}

	public Response responseRequest() {
		String html = getHTMLAfterI18n(GlobalParameter.PATH_RESPONSE);

		return Response.ok(html).build();
	}

	public String getNotification(String retrivingPath) {
		String html = getHTMLAfterI18n(GlobalParameter.PATH_NOTIFICATION);

		html = html.replaceAll("\\$\\{retrivingPath\\}", retrivingPath);

		return html;
	}

	private String getHTMLAfterI18n(String path) {
		InputStream in = getClass().getResourceAsStream("../" + path);

		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		StringBuffer htmlBuffer = new StringBuffer();
		String str = null;
		try {
			while ((str = br.readLine()) != null) {
				htmlBuffer.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String html = htmlBuffer.toString();

		html = i18n(html);

		return html;
	}
	
	private String i18n(String content) {
		Enumeration<String> keys = bundle.getKeys();
		String key;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			content = content.replaceAll("\\$\\{" + key + "\\}", bundle.getString(key));
		}

		return content;
	}
}
