package resource;

import util.GlobalParameter;
import util.PagePIDExtractor;
import util.SerialPagePIDExtractor;

public class FedoraObjectService {

	protected PagePIDExtractor pidExtracoteor = new SerialPagePIDExtractor();

	protected final static String PAGE_DEF = "demo:pageSdef";
	
	public String getURLFromPID(String pid) {
		return GlobalParameter.BASE_URL + "/fedora/objects/" + pid;
	}
}
