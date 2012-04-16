package com.bhle.access.storage.akubra.mapper.path;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;

import com.bhle.access.util.FedoraURI;

public class PairtreePathMapper implements PathMapper {

	private int fixedPathLength;

	public PairtreePathMapper(int fixedPathLength) {
		this.fixedPathLength = fixedPathLength;
	}

	public String getInternalPath(URI externalId) {
		FedoraURI fedoraURI = new FedoraURI(externalId);
		StringBuffer fullPath = new StringBuffer();

		String guid = fedoraURI.getGuid();

		int pathLengthCount = 0;
		for (int i = 0; i < guid.length(); i++) {
			fullPath.append(guid.charAt(i));
			pathLengthCount++;
			if (pathLengthCount == fixedPathLength && i != guid.length() - 1) {
				fullPath.append("/");
				pathLengthCount = 0;
			}
		}
		fullPath.append("/" + guid);
		return fullPath.toString();
	}

	public String getExternalPath(URI internalId, String namespace) {
		String path = internalId.getSchemeSpecificPart();
		String pid = null;
		int lashSlash = path.lastIndexOf("/");
		String fileName = path.substring(lashSlash + 1);
		Pattern pattern = Pattern.compile("^([^_/]+)_(\\S+)\\.(\\w+)$");
		Matcher matcher = pattern.matcher(fileName);

		if (matcher.find()) {
			if (Pattern.matches("\\d+", matcher.group(2))) {
				pid = FedoraURI.getPidFromGuid(matcher.group(1)) + "-"
						+ matcher.group(2);
				return pid;
			} else {
				pid = FedoraURI.getPidFromGuid(matcher.group(1));
				return pid;
			}
		} else {
			return null;
		}
	}

	public String getInternalPrefix(String externalPrefix) {
		StringBuffer internalPrefix = new StringBuffer();

		int pathLengthCount = 0;
		for (int i = 0; i < externalPrefix.length(); i++) {
			internalPrefix.append(externalPrefix.charAt(i));
			pathLengthCount++;
			if (pathLengthCount == fixedPathLength
					&& i != externalPrefix.length() - 1) {
				internalPrefix.append("/");
				pathLengthCount = 0;
			}
		}
		internalPrefix.append("/");
		return internalPrefix.toString();
	}

}
