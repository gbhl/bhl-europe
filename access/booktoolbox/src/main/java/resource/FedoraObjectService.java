package resource;

import util.GlobalParameter;
import util.PagePIDExtractor;
import util.Resolution;
import util.SerialPagePIDExtractor;

public class FedoraObjectService {
	protected PagePIDExtractor pidExtracoteor = new SerialPagePIDExtractor();

	public static String getURLFromPID(String pid) {
		return GlobalParameter.URL_FEDORA + "/objects/" + pid;
	}

	public static String getOCRURLFromPID(String pid) {
		return getURLFromPID(pid) + "/datastreams/" + GlobalParameter.DS_ID_OCR
				+ "/content";
	}

	public static String getBoxURLFromPID(String pid) {
		return getURLFromPID(pid) + "/datastreams/"
				+ GlobalParameter.DS_ID_COORDINATE + "/content";
	}

	public static String getJPEGURLFromPID(String pid, int level) {
		return getURLFromPID(pid) + "/methods/" + GlobalParameter.PID_PAGE_SDEF
				+ "/jpeg?" + GlobalParameter.METHOD_JPEG_LEVEL + "=" + level;
	}

	public static String getPDFURLFromPID(String pid, Resolution resolution) {
		return getURLFromPID(pid) + "/methods/" + GlobalParameter.PID_PAGE_SDEF
				+ "/pdf?" + GlobalParameter.METHOD_PDF_RESOLUTION + "="
				+ resolution.getResolution();
	}
}
