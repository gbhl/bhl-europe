package com.bhle.access.storage.akubra.mapper.file;

import java.net.URI;
import java.util.regex.Pattern;

import com.bhle.access.convert.ConvertorManager;
import com.bhle.access.util.FedoraURI;

public class SubFolderFileMapper implements FileMapper {

	public String getInternalFile(URI externalId) {
		FedoraURI fedoraURI = new FedoraURI(externalId);
		String guid = fedoraURI.getGuid();
		String serialNumber = fedoraURI.getSerialNumber();
		String dsid = fedoraURI.getDsid();

		StringBuffer fileName = new StringBuffer();

		if (serialNumber != null) {
			if (dsid == null) {
				fileName.append("*/");
			} else {
				fileName.append(dsid.toLowerCase() + "/");
			}
		}

		fileName.append(guid);

		if (serialNumber != null) {
			fileName.append("_" + serialNumber);
		} else if (dsid != null){
			fileName.append("_" + dsid.toLowerCase());
		} else {
			fileName.append("_*");
		}
		
		String suffix = ConvertorManager.getSuffix(fedoraURI.getDsid());
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
		String[] fileNameFrags = fileName.split("[_\\.]");
		
		
		if (fileNameFrags.length == 3) {
			if (Pattern.matches("\\d+", fileNameFrags[1])){
				int secondToLast = path.lastIndexOf("/", lashSlash -1);
				dsid = path.substring(secondToLast + 1, lashSlash);
				return dsid.toUpperCase();
			} else {
				dsid = fileNameFrags[1];
				return dsid.toUpperCase();
			}
		} else {
			return null;
		}
	}
}
