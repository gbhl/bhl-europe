package com.atos.bookingester.rebuild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.atos.bookingester.util.ImageUtil;

public class Page implements FedoraObject {
	private String ID;
	private String TAXA;
	private String OCR;
	private Book book;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getTAXA() {
		return TAXA;
	}

	public void setTAXA(String tAXA) {
		TAXA = tAXA;
	}

	public String getOCR() {
		return OCR;
	}

	public void setOCR(String oCR) {
		OCR = oCR;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	private String image;

	public DublinCore getDC() {
		DublinCore dc = new DublinCore();
		dc.setId(ID);
		// TODO More details of the page title
		dc.setTitle(ID);
		return dc;
	}

	public List<ExternalRelationship> getRelExts() {
		List<ExternalRelationship> rels = new ArrayList<ExternalRelationship>();
		ExternalRelationship modelRel = new ExternalRelationship();
		modelRel.setPredicate("info:fedora/fedora-system:def/model#hasModel");
		modelRel.setSubject("info:fedora/ilives:pageCModel");
		rels.add(modelRel);

		ExternalRelationship memberRel = new ExternalRelationship();
		memberRel.setPredicate("info:fedora/fedora-system:def/relations-external#isMemberOf");
		memberRel.setSubject("info:fedora/" + this.getBook().getDC().getId());
		rels.add(memberRel);
		return rels;
	}

	public Map<String, DataStream> getDatastreams() {
		Map<String, DataStream> result = new Hashtable<String, DataStream>();

		rebuildJP2(result);
		rebuildTIFF(result);
		rebuildTEI(result);
		rebuildTN(result);

		return result;
	}

	private void rebuildTN(Map<String, DataStream> datastreams) {
		if (image != null && !image.equals("")) {
			DataStream ds = new DataStream();
			ds.setMimeType("image/jpg");
			ds.setLabel("Thumbnail");
			ds.setType(DatastreamContentType.FILE);
			ds.setContent(ImageUtil.resizeToTN(image));

			datastreams.put("TN", ds);
		}
	}

	private void rebuildTIFF(Map<String, DataStream> datastreams) {
		if (image != null && !image.equals("")) {
			DataStream ds = new DataStream();
			ds.setMimeType("image/tiff");
			ds.setLabel("Archival TIFF");
			ds.setType(DatastreamContentType.URL);
			if (ImageUtil.isTIFF(image)) {
				ds.setLocation(image);
			}

			datastreams.put("TIFF", ds);
		}
	}

	private void rebuildTEI(Map<String, DataStream> datastreams) {
		if (OCR != null && !OCR.equals("")) {
			DataStream ds = new DataStream();
			ds.setMimeType("text/plain");
			ds.setLabel("OCR Text");
			ds.setType(DatastreamContentType.URL);
			ds.setLocation(OCR);

			datastreams.put("TEI", ds);
		}
	}

	private void rebuildJP2(Map<String, DataStream> datastreams) {
		if (image != null && !image.equals("")) {
			DataStream ds = new DataStream();
			ds.setMimeType("image/jp2");
			ds.setLabel("JP2000");
			if (ImageUtil.isJP2(image)) {
				ds.setType(DatastreamContentType.URL);
				ds.setLocation(image);
			} else {
				ds.setType(DatastreamContentType.FILE);
				ds.setContent(ImageUtil.convertToJP2(image));
			}

			datastreams.put("JP2", ds);
		}
	}

	public List<FedoraObject> getChildren() {
		return Collections.emptyList();
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

}
