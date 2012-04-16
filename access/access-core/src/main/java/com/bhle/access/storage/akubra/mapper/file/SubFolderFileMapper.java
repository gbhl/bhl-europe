package com.bhle.access.storage.akubra.mapper.file;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.convert.ConverterManager;
import com.bhle.access.util.FedoraURI;

public class SubFolderFileMapper implements FileMapper {

	private static final Logger logger = LoggerFactory
			.getLogger(SubFolderFileMapper.class);

	public String getInternalFile(URI externalId) {
		FedoraURI fedoraURI = new FedoraURI(externalId);
		String guid = fedoraURI.getGuid();
		String serialNumber = fedoraURI.getSerialNumber();
		String dsid = fedoraURI.getDsid();

		StringBuffer fileName = new StringBuffer();

		if (serialNumber != null && !serialNumber.equals("")) {
			if (dsid != null && !dsid.equals("")) {
				fileName.append(dsid.toLowerCase() + "/");
			} else {
				fileName.append("*/");
			}
		}

		fileName.append(guid);

		if (serialNumber != null && !serialNumber.equals("")) {
			fileName.append("_" + serialNumber);
		} else if (dsid != null && !dsid.equals("")) {
			fileName.append("_" + dsid.toLowerCase());
		} else {
			fileName.append("_*");
		}

		String suffix = ConverterManager.getSuffix(fedoraURI.getDsid());
		if (suffix != null) {
			fileName.append("." + suffix);
		} else {
			fileName.append(".*");
		}
		return fileName.toString();
	}

	public String getExternalFile(URI internalId) {
		String path = internalId.getSchemeSpecificPart();
		String dsid = null;
		int lashSlash = path.lastIndexOf("/");
		String fileName = path.substring(lashSlash + 1);
		Pattern pattern = Pattern.compile("^([^_/]+)_(\\S+)\\.(\\w+)$");
		Matcher matcher = pattern.matcher(fileName);

		if (matcher.find()) {
			if (Pattern.matches("\\d+", matcher.group(2))) {
				int secondToLast = path.lastIndexOf("/", lashSlash - 1);
				dsid = path.substring(secondToLast + 1, lashSlash);
				return dsid.toUpperCase();
			} else {
				dsid = matcher.group(2);
				return dsid.toUpperCase();
			}
		} else {
			return null;
		}
	}
}
