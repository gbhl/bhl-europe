package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Definition;
import util.GlobalParameter;
import util.PagePIDExtractor;
import util.Range;
import util.SerialPagePIDExtractor;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class BookPDFServlet extends HttpServlet {

	private PagePIDExtractor pidExtracoteor = new SerialPagePIDExtractor();
	private String pid;
	private String ranges;
	private String definition;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.pid = req.getParameter("pid");
		this.definition = req.getParameter("definition");
		this.ranges = req.getParameter("ranges");
		
		List<String> pagePIDs = pidExtracoteor.getPIDs(pid, ranges);
		if (pagePIDs.isEmpty()){
			resp.sendError(404, "The range(s) is not found");
		}

		configResponse(resp);

		outputPDF(pagePIDs, resp);
	}

	private void configResponse(HttpServletResponse resp) {
		resp.setContentType("application/pdf");
		resp.setHeader("Content-Disposition", "attachment;filename=" + pid
				+ ".pdf");
	}

	private void outputPDF(List<String> pagePIDs, HttpServletResponse resp) {
		Document document = initialPDF();

		try {
			PdfWriter writer = PdfWriter.getInstance(document,
					resp.getOutputStream());
			document.open();
			mergeAllPages(pagePIDs, writer, document);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}

	}

	private void mergeAllPages(List<String> pagePIDs, PdfWriter writer,
			Document document){
		PdfContentByte cb = writer.getDirectContent();

		for (int i = 0; i < pagePIDs.size(); i++) {
			try {
				URL pdfURL = new URL(getURLFromBookPID(pagePIDs.get(i))
						+ "/methods/demo:pageSdef/pdf?definition=" + definition);
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

	private String getURLFromBookPID(String pid) {
		return GlobalParameter.BASE_URL + "/fedora/objects/" + pid;
	}
}
