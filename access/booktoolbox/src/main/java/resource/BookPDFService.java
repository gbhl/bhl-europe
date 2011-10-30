package resource;

import java.io.IOException;
import java.io.OutputStream;
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
import util.Resolution;
import util.SerialPagePIDExtractor;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

@Path("book/pdf")
public class BookPDFService extends FedoraObjectService{
	@GET
	@Path("{pid}")
	@Produces("application/pdf")
	public StreamingOutput getPDF(
			@PathParam("pid") String pid,
			@DefaultValue("") @QueryParam("ranges") String ranges,
			@DefaultValue("medium") @QueryParam("resolution") final Resolution resolution) {

		final List<String> rangeList = pidExtracoteor.getPIDs(pid, ranges);

		return new StreamingOutput() {

			public void write(OutputStream out) throws IOException,
					WebApplicationException {
				createPDF(out, rangeList, resolution);
			}
		};
	}

	private void createPDF(OutputStream out, List<String> rangeList,
			Resolution resolution) {
		Document document = initialPDF();

		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			mergeAllPages(rangeList, resolution, writer, document);
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

	private void mergeAllPages(List<String> pagePIDs, Resolution resolution,
			PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();

		for (int i = 0; i < pagePIDs.size(); i++) {
			try {
				URL pdfURL = new URL(getPDFURLFromPID(pagePIDs.get(i), resolution));
				PdfReader pdfReader = new PdfReader(pdfURL);
				PdfTemplate page = writer.getImportedPage(pdfReader, 1);
				document.newPage();
				cb.addTemplate(page, 0, 0);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Document initialPDF() {
		Document document = new Document();
		document.setMargins(0, 0, 0, 0);
		return document;
	}
}
