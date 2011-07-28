package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.GlobalParameter;
import util.PagePIDExtractor;
import util.SerialPagePIDExtractor;

public class BookOCRServlet extends HttpServlet {

	private PagePIDExtractor pidExtracoteor = new SerialPagePIDExtractor();
	private String pid;
	private String ranges;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.pid = req.getParameter("pid");
		this.ranges = req.getParameter("ranges");

		List<String> pagePIDs = pidExtracoteor.getPIDs(pid, ranges);
		if (pagePIDs.isEmpty()){
			resp.sendError(404, "The range(s) is not found");
		}

		configResponse(resp);

		outputText(pagePIDs, resp);
	}

	private void configResponse(HttpServletResponse resp) {
		resp.setContentType("text/plain");
		resp.setHeader("Content-Disposition", "attachment;filename=" + pid
				+ ".txt");

	}

	private void outputText(List<String> pagePIDs, HttpServletResponse resp) {
		PrintWriter writer;
		try {
			writer = resp.getWriter();
			for (String pid : pagePIDs) {
				writePageOCR(pid, writer);
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writePageOCR(String pid, PrintWriter writer) {

		try {
			URL url = new URL(GlobalParameter.BASE_URL + "/fedora/objects/"
					+ pid + "/datastreams/TEI/content");
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
