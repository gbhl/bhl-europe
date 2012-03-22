package com.bhle.access.download;

import java.io.IOException;
import java.io.OutputStream;

import org.akubraproject.Blob;
import org.akubraproject.DuplicateBlobException;
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

		switch (request.getContentType()) {
		case PDF:
			try {
				Blob blob = storage.getBlob((OfflineDownloadRequest) request);
				OutputStream out = null;
				try {
					out = blob.openOutputStream(-1, true);
				} catch (DuplicateBlobException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				PdfGenerator.generate(request.getPageURIs(),
						request.getResolution(), out);
				return responseBuilderFactory.createBuilder(request).build(
						request, blob, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		case JPEG:
			Blob blob = storage.getBlob((OfflineDownloadRequest) request);
			OutputStream out = null;
			try {
				out = blob.openOutputStream(-1, true);
			} catch (DuplicateBlobException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				JpegPackageGenerator.generate(request.getPageURIs(),
						request.getResolution(), out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return responseBuilderFactory.createBuilder(request).build(request,
					blob, null);
		case OCR:
			byte[] bytes = null;
			try {
				bytes = OcrGenerator.generate(request.getPageURIs());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return responseBuilderFactory.createBuilder(request).build(request,
					null, bytes);
		default:
			throw new IllegalArgumentException();
		}
	}
}
