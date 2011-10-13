package resource;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import util.DjatokaImageConverter;
import util.ImageConverter;
import util.Resolution;

@Path("page/pdf")
public class PagePDFService {

	private ImageConverter converter = new DjatokaImageConverter();;

	@GET
	@Path("{pid}")
	@Produces("application/pdf")
	public StreamingOutput getPDF(
			@PathParam("pid") final String pid,
			@DefaultValue("medium") @QueryParam("resolution") final Resolution resolution) {
		return new StreamingOutput() {

			public void write(OutputStream out) throws IOException,
					WebApplicationException {
				Image image = converter.convertFromPID(pid, resolution);
				createPDF(out, image);
			}
		};
	}

	private void createPDF(OutputStream out, Image image) {
		Document document = initialPDF();
		try {
			PdfWriter.getInstance(document, out);
			document.open();
			configureImage(image, document);
			document.add(image);
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

	private void configureImage(Image image, Document document) {
		image.setAlignment(Element.ALIGN_CENTER);
		image.scaleToFit(document.getPageSize().getWidth(), document
				.getPageSize().getHeight());
	}

	private Document initialPDF() {
		Document document = new Document();
		document.setMargins(0, 0, 0, 0);
		return document;
	}

}
