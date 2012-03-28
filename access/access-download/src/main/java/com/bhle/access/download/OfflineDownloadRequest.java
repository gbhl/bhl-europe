package com.bhle.access.download;

import java.net.URI;

import com.bhle.access.download.generator.JpegPackageGenerator;
import com.bhle.access.download.generator.PdfGenerator;
import com.bhle.access.util.FedoraURI;

public class OfflineDownloadRequest extends BasicDownloadRequest {
	String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public OfflineDownloadRequest() {
	}

	public OfflineDownloadRequest(ContentType contentType, String[] pageURIs,
			Resolution resolution, String email) {
		super(contentType, pageURIs, resolution);
		this.email = email;
	}

	public String generateFilename() {
		String suffix = null;
		switch (contentType) {
		case JPEG:
			suffix = JpegPackageGenerator.SUFFIX;
			break;
		case PDF:
			suffix = PdfGenerator.SUFFIX;
			break;
		default:
			throw new IllegalArgumentException("Cannot define file suffix");
		}
		FedoraURI fedoraUri = new FedoraURI(URI.create(this.pageURIs[0]));
		return fedoraUri.getGuid() + "." + suffix;
	}
}
