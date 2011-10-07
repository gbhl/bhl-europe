package resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import util.SearchResult;
import util.StringWithBox;

@Path("book/search")
public class BookSearchService extends FedoraObjectService {
	@GET
	@Path("{pid}")
	@Produces("application/json")
	public String search(@PathParam("pid") String pid,
			@QueryParam("query") String querys) {
		String[] querysArray = querys.split(" ");
		
		SearchResult result = new SearchResult();
		for (StringWithBox pageText : getPageTexts(pid)) {
			for (String query : querysArray) {
				if (pageText.contains(query)) {
					pageText.populateWithBox();
					result.add(pageText, query);
				}
			}
		}
		return result.toJSON();
	}

	private List<StringWithBox> getPageTexts(String pid) {
		List<String> pagePidList = pidExtracoteor.getPIDs(pid, null);

		List<StringWithBox> pageTexts = new ArrayList<StringWithBox>();

		for (String pagePid : pagePidList) {
			pageTexts.add(new StringWithBox(pagePid, getPageOCR(pagePid)));
		}

		return pageTexts;
	}

	private String getPageOCR(String pid) {
		StringBuffer ocr = new StringBuffer();

		try {
			URL url = new URL(getOCRURLFromPID(pid));
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));

			String str;
			while ((str = br.readLine()) != null) {
				ocr.append(str);
			}
			br.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ocr.toString();
	}
}
