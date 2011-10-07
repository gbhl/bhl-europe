package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import util.DjatokaImageConverter;
import util.ImageConverter;
import util.Resolution;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class PagePDFServlet extends HttpServlet {

	private ImageConverter converter;

	public PagePDFServlet() {
		super();
		this.converter = new DjatokaImageConverter();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String source = req.getParameter("source");
		String definition = req.getParameter("definition");
		Image image = converter.convertFromSource(source, new Resolution(definition));
		outputPDF(image, resp);
	}

	private void configResponse(HttpServletResponse resp) {
		resp.setContentType("application/pdf");
	}

	private void outputPDF(Image image, HttpServletResponse resp) {
		configResponse(resp);
		
		Document document = initialPDF();

		try {
			PdfWriter.getInstance(document, resp.getOutputStream());
			document.open();
			image.setAlignment(Element.ALIGN_CENTER);
			image.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
			document.add(image);
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Document initialPDF() {
		Document document = new Document();
		document.setMargins(0, 0, 0, 0);
		return document;
	}

}
