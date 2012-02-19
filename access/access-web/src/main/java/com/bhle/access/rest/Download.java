package com.bhle.access.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;

import com.bhle.access.bookreader.BookInfoBuilder;
import com.bhle.access.bookreader.ThumbnailBuilder;
import com.bhle.access.download.JpegPackageGenerator;
import com.bhle.access.download.OcrGenerator;
import com.bhle.access.download.PageURIExtractor;
import com.bhle.access.download.PageURIExtractorImpl;
import com.bhle.access.download.PdfGenerator;
import com.bhle.access.download.Resolution;
import com.bhle.access.download.offline.Offlinable;

@Path("download")
public class Download {

	private static PageURIExtractor PID_EXTRACTOR = new PageURIExtractorImpl();

	@GET
	@Path("{guid}/jpg")
	@Produces("application/x-zip-compressed")
	@Offlinable()
	public Response downloadJpeg(
			@PathParam("guid") String guid,
			@DefaultValue("") @QueryParam("ranges") String ranges,
			@DefaultValue("medium") @QueryParam("resolution") Resolution resolution) {
		String[] pageURIs = PID_EXTRACTOR.getPageURIs(guid, ranges);
		byte[] bytes;
		try {
			bytes = JpegPackageGenerator.generate(pageURIs, resolution);
			return Response.ok(wrapByteArrayAsStreamingOutput(bytes)).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	@GET
	@Path("{guid}/pdf")
	@Produces("application/pdf")
	@Offlinable()
	public Response downloadPdf(
			@PathParam("guid") String guid,
			@DefaultValue("") @QueryParam("ranges") String ranges,
			@DefaultValue("medium") @QueryParam("resolution") Resolution resolution) {
		String[] pageURIs = PID_EXTRACTOR.getPageURIs(guid, ranges);
		byte[] bytes;
		try {
			bytes = PdfGenerator.generate(pageURIs, resolution);
			return Response.ok(wrapByteArrayAsStreamingOutput(bytes)).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	@GET
	@Path("{guid}/ocr")
	@Produces("text/plain")
	public Response downloadOcr(
			@PathParam("guid") String guid,
			@DefaultValue("") @QueryParam("ranges") String ranges) {
		String[] pageURIs = PID_EXTRACTOR.getPageURIs(guid, ranges);
		byte[] bytes;
		try {
			bytes = OcrGenerator.generate(pageURIs);
			return Response.ok(wrapByteArrayAsStreamingOutput(bytes)).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	@GET
	@Path("{guid}/thumbnail")
	@Produces("image/jpeg")
	public Response downloadThumbnail(
			@PathParam("guid") String guid) {
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

	private StreamingOutput wrapByteArrayAsStreamingOutput(final byte[] bytes) {
		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				output.write(bytes);
			}
		};
	}
}
