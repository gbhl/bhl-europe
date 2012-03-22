package com.bhle.access.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bhle.access.bookreader.ThumbnailBuilder;
import com.bhle.access.download.BasicDownloadRequest;
import com.bhle.access.download.ContentType;
import com.bhle.access.download.DownloadGateway;
import com.bhle.access.download.DownloadLocationHelper;
import com.bhle.access.download.DownloadResponse;
import com.bhle.access.download.OfflineDownloadRequest;
import com.bhle.access.download.OfflineDownloadResponse;
import com.bhle.access.download.OfflineDownloadResponssFetcher;
import com.bhle.access.download.OnlineDownloadResponse;
import com.bhle.access.download.Resolution;
import com.bhle.access.download.generator.OcrGenerator;
import com.bhle.access.download.generator.PageURIExtractor;
import com.bhle.access.download.generator.PageURIExtractorImpl;
import com.sun.jersey.api.NotFoundException;

@Path("download")
public class Download {

	private static final Logger logger = LoggerFactory
			.getLogger(Download.class);

	private static PageURIExtractor PID_EXTRACTOR = new PageURIExtractorImpl();

	@Autowired
	private DownloadGateway downloadGateway;

	@Autowired
	private OfflineDownloadResponssFetcher downloadResponseFetcher;

	@POST
	@Path("{guid}/jpg")
	@Produces("application/x-zip-compressed")
	public Response downloadJpeg(
			@PathParam("guid") String guid,
			@DefaultValue("") @FormParam("ranges") String ranges,
			@DefaultValue("medium") @FormParam("resolution") Resolution resolution,
			@FormParam("email") String email) {
		String[] pageURIs = PID_EXTRACTOR.getPageURIs(guid, ranges);
		downloadGateway.download(new OfflineDownloadRequest(ContentType.JPEG,
				pageURIs, resolution, email));
		return Response.ok().build();
	}

	@POST
	@Path("{guid}/pdf")
	@Produces("application/pdf")
	public Response downloadPdf(
			@PathParam("guid") String guid,
			@DefaultValue("") @FormParam("ranges") String ranges,
			@DefaultValue("medium") @FormParam("resolution") Resolution resolution,
			@FormParam("email") String email) {
		String[] pageURIs = PID_EXTRACTOR.getPageURIs(guid, ranges);
		logger.info(ranges);
		downloadGateway.download(new OfflineDownloadRequest(ContentType.PDF,
				pageURIs, resolution, email));
		return Response.ok().build();
	}

	@GET
	@Path("{guid}/ocr")
	@Produces("text/plain")
	public Response downloadOcr(@PathParam("guid") String guid,
			@DefaultValue("") @QueryParam("ranges") String ranges) {
		String[] pageURIs = PID_EXTRACTOR.getPageURIs(guid, ranges);
		byte[] bytes;
		try {
			bytes = OcrGenerator.generate(pageURIs);
			return Response.ok(wrapByteArrayAsStreamingOutput(bytes)).build();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Response.serverError().build();
	}

	@GET
	@Path("{guid}/thumbnail")
	@Produces("image/jpeg")
	public Response downloadThumbnail(@PathParam("guid") String guid) {
		byte[] bytes = null;
		if (ThumbnailBuilder.exists(guid)) {
			try {
				bytes = IOUtils.toByteArray(ThumbnailBuilder.read(guid));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			InputStream is = ThumbnailBuilder.build(guid);
			try {
				bytes = IOUtils.toByteArray(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ThumbnailBuilder.save(guid, new ByteArrayInputStream(bytes));
		}

		return Response.ok(wrapByteArrayAsStreamingOutput(bytes)).build();
	}

	@GET
	@Path("offline")
	public Response offlineDownload(@QueryParam("path") String path) {

		logger.info(path);

		String plainText = DownloadLocationHelper.decrypt(path);
		String[] parts = plainText.split("/");
		if (parts.length != 2) {
			throw new NotFoundException();
		}
		String email = parts[0];
		String filename = parts[1];

		logger.info("{} {}", email, filename);

		try {
			final OfflineDownloadResponse response = downloadResponseFetcher
					.fetch(email, filename);
			return Response
					.ok(new StreamingOutput() {
						@Override
						public void write(OutputStream output)
								throws IOException, WebApplicationException {
							IOUtils.copy(response.getBlob().openInputStream(),
									output);
						}
					})
					.header("Content-Disposition",
							"attachment; filename=" + filename).build();
		} catch (IOException e) {
			throw new NotFoundException();
		}
	}

	private StreamingOutput wrapByteArrayAsStreamingOutput(final byte[] bytes) {
		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				output.write(bytes);
			}
		};
	}
}
