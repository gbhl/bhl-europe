package resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import util.GlobalParameter;
import util.PagePIDExtractor;
import util.Resolution;
import util.SerialPagePIDExtractor;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

@Path("book/zip")
public class BookZIPService extends FedoraObjectService {
	@GET
	@Path("{pid}")
	@Produces("application/zip")
	public Response getPDF(
			@PathParam("pid") String pid,
			@DefaultValue("") @QueryParam("ranges") String ranges,
			@DefaultValue("medium") @QueryParam("resolution") final Resolution resolution) {

		final List<String> rangeList = pidExtracoteor.getPIDs(pid, ranges);
		return Response.ok(new StreamingOutput() {

			public void write(OutputStream out) throws IOException,
					WebApplicationException {
				createZIP(out, rangeList, resolution);
			}
		}).header("Content-Disposition", "attachment;filename=" + pid
				+ ".zip").build();
//		return new StreamingOutput() {
//
//			public void write(OutputStream out) throws IOException,
//					WebApplicationException {
//				createZIP(out, rangeList, resolution);
//			}
//		};
	}

	private void createZIP(OutputStream out, List<String> rangeList,
			Resolution resolution) {
		try {
			ZipOutputStream zipOut = new ZipOutputStream(out);
			for (String pid : rangeList) {
				AddPageEntry(pid, resolution, zipOut);
			}
			zipOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void AddPageEntry(String pid, Resolution resolution,
			ZipOutputStream out) {
		URL url;
		try {
			url = new URL(getJPEGURLFromPID(pid, resolution.getLevel()));
			InputStream in = url.openStream();

			ZipEntry entry = new ZipEntry(pid + ".jpg");
			out.putNextEntry(entry);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			out.closeEntry();
			in.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
