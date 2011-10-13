package servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import resource.FedoraObjectService;

import util.Definition;
import util.GlobalParameter;
import util.PagePIDExtractor;
import util.SerialPagePIDExtractor;

public class BookZIPServlet extends HttpServlet {

	private PagePIDExtractor pidExtracoteor = new SerialPagePIDExtractor();
	private String pid;
	private String ranges;
	private String definition;
	private int level;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.pid = req.getParameter("pid");
		this.definition = req.getParameter("definition");
		this.ranges = req.getParameter("ranges");
		if (definition.equalsIgnoreCase("high")){
			this.level = Definition.HIGH.getLevel();
		} else if (definition.equalsIgnoreCase("low")){
			this.level = Definition.LOW.getLevel();
		} else {
			this.level = Definition.MEDIUM.getLevel();
		}

		List<String> pagePIDs = pidExtracoteor.getPIDs(pid, ranges);
		if (pagePIDs.isEmpty()){
			resp.sendError(404, "The range(s) is not found");
		}

		configResponse(resp);

		outputZip(pagePIDs, resp);
	}

	private void configResponse(HttpServletResponse resp) {
		resp.setContentType("application/zip");
		resp.setHeader("Content-Disposition", "attachment;filename=" + pid
				+ ".zip");

	}

	private void outputZip(List<String> pagePIDs, HttpServletResponse resp) {
		try {
			ZipOutputStream out = new ZipOutputStream(resp.getOutputStream());
			for (String pid : pagePIDs) {
				AddPageEntry(pid, out);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void AddPageEntry(String pid, ZipOutputStream out) {
		URL url;
		try {
			url = new URL(FedoraObjectService.getJPEGURLFromPID(pid, level));
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
