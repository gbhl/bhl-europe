package util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import resource.FedoraObjectService;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

public class DjatokaImageConverter implements ImageConverter {

	public Image convert(String pid) {
		return convertFromPID(pid, new Resolution("medium"));
	}

	public Image convertFromSource(String source, Resolution resolution) {			
		Image result = null;
		try {
			result = Image
					.getInstance(new URL(
							GlobalParameter.BASE_URL
									+ "/adore-djatoka/resolver?url_ver=Z39.88-2004&rft_id="
									+ source
									+ "&svc_id=info:lanl-repo/svc/getRegion&svc_val_fmt=info:ofi/fmt:kev:mtx:jpeg2000&svc.format=image/jpeg&svc.level="
									+ resolution.getLevel() + "&svc.rotate=0"));
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Image convertFromPID(String pid, Resolution resolution) {
		return convertFromSource(FedoraObjectService.getURLFromPID(pid) + "/datastreams/JP2/content", resolution);
	}
}
