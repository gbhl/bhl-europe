package resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import util.GlobalParameter;
import util.PagePIDExtractor;
import util.SerialPagePIDExtractor;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

@Path("book/ocr")
public class BookOCRService extends FedoraObjectService{
	@GET
	@Path("{pid}")
	@Produces("text/plain")
	public StreamingOutput getPDF(
			@PathParam("pid") String pid,
			@DefaultValue("pdf") @QueryParam("format") final String format,
			@DefaultValue("") @QueryParam("ranges") String ranges) {

		final List<String> rangeList = pidExtracoteor.getPIDs(pid, ranges);

		return new StreamingOutput() {

			public void write(OutputStream out) throws IOException,
					WebApplicationException {
					createOCR(out, rangeList);
			}
		};
	}


	private void createOCR(OutputStream out, List<String> rangeList) {
		PrintWriter writer = new PrintWriter(out);

		for (String pid : rangeList) {
			writePageOCR(pid, writer);
		}

		writer.close();
	}
	
	private void writePageOCR(String pid, PrintWriter writer) {

		try {
			URL url = new URL(getOCRURLFromPID(pid));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String str;
			while ((str = in.readLine()) != null) {
				writer.println(str);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
