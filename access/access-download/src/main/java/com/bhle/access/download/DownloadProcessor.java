package com.bhle.access.download;

import java.io.IOException;
import java.io.OutputStream;

import org.akubraproject.Blob;
import org.akubraproject.DuplicateBlobException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import com.bhle.access.download.generator.JpegPackageGenerator;
import com.bhle.access.download.generator.OcrGenerator;
import com.bhle.access.download.generator.PdfGenerator;
import com.bhle.access.download.storage.FileStorage;

@Component
public class DownloadProcessor {

	private static final Logger logger = LoggerFactory
			.getLogger(DownloadProcessor.class);

	@Autowired
	private FileStorage storage;

	@Autowired
	private DownloadResponseBuilderFactory responseBuilderFactory;

	@ServiceActivator
	public DownloadResponse process(DownloadRequest request) {

		Blob blob = request.getBlob();
		if (blob == null && request.isOffline()) {
			blob = storage.fetchBlob((OfflineDownloadRequest) request);
		}
		OutputStream out = null;
		try {
			out = blob.openOutputStream(-1, true);
		} catch (DuplicateBlobException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		switch (request.getContentType()) {
		case PDF:
			try {
				PdfGenerator.generate(request.getPageURIs(),
						request.getResolution(), out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			IOUtils.closeQuietly(out);
			return responseBuilderFactory.createBuilder(request).build(request,
					blob);
		case JPEG:
			try {
				JpegPackageGenerator.generate(request.getPageURIs(),
						request.getResolution(), out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			IOUtils.closeQuietly(out);
			return responseBuilderFactory.createBuilder(request).build(request,
					blob);
		case OCR:
			try {
				OcrGenerator.generate(request.getPageURIs(), out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			IOUtils.closeQuietly(out);
			return responseBuilderFactory.createBuilder(request).build(request,
					blob);
		default:
			throw new IllegalArgumentException();
		}
	}
}
