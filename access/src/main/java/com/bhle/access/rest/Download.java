package com.bhle.access.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

import com.bhle.access.download.JpegPackageGenerator;
import com.bhle.access.download.OcrGenerator;
import com.bhle.access.download.PageURIExtractor;
import com.bhle.access.download.PageURIExtractorImpl;
import com.bhle.access.download.PdfGenerator;
import com.bhle.access.download.Resolution;
import com.bhle.access.download.offline.Offlinable;
import com.bhle.access.download.offline.OfflineProductType;

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

	private StreamingOutput wrapByteArrayAsStreamingOutput(final byte[] bytes) {
		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				output.write(bytes);
			}
		};
	}
}
