package com.atos.bookingester.rebuild;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.atos.bookingester.util.ImageUtil;

public class Book implements FedoraObject {
	private String title;
	private String ID;
	private String MODS;
	private List<Page> pages;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getMODS() {
		return MODS;
	}

	public void setMODS(String mODS) {
		MODS = mODS;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public DublinCore getDC() {
		DublinCore dc = new DublinCore();
		dc.setId(ID);
		dc.setTitle(title);
		return dc;
	}

	public List<ExternalRelationship> getRelExts() {
		List<ExternalRelationship> rels = new ArrayList<ExternalRelationship>();
		ExternalRelationship modelRel = new ExternalRelationship();
		modelRel.setPredicate("info:fedora/fedora-system:def/model#hasModel");
		modelRel.setSubject("info:fedora/ilives:bookCModel");

		rels.add(modelRel);
		return rels;
	}

	public Map<String, DataStream> getDatastreams() {
		Map<String, DataStream> result = new Hashtable<String, DataStream>();
		result.put("MODS", rebuildMODS());
		result.put("TN", rebuildTN());
		return result;
	}

	private DataStream rebuildTN() {
		DataStream ds = new DataStream();
		ds.setLabel("Thumbnail");
		ds.setMimeType("image/jpg");
		ds.setType(DatastreamContentType.FILE);
		ds.setContent(ImageUtil.resizeToTN(pages.get(0).getImage()));
		return ds;
	}

	private DataStream rebuildMODS() {
		DataStream ds = new DataStream();
		ds.setLabel("MODS Metadata");
		ds.setMimeType("text/xml");
		ds.setType(DatastreamContentType.XML);
		ds.setContent(MODS);
		return ds;
	}

	public List<FedoraObject> getChildren() {
		List<FedoraObject> pageObjects = new ArrayList<FedoraObject>();
		for (Page page : pages) {
			pageObjects.add(page);
		}
		return pageObjects;
	}

}
