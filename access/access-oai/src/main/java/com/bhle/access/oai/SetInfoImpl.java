package com.bhle.access.oai;

import java.io.PrintWriter;

import proai.SetInfo;
import proai.error.ServerException;

public class SetInfoImpl implements SetInfo {

	private String setSpec;
	private String setName;

	private String contentModel;

	public SetInfoImpl(String setSpec, String setName, String contentModel) {
		super();
		this.setSpec = setSpec;
		this.setName = setName;
		this.contentModel = contentModel;
	}

	@Override
	public void write(PrintWriter out) throws ServerException {
		out.write("<set>" + "<setSpec>" + setSpec + "</setSpec>" + "<setName>"
				+ setName + "</setName>" + "</set>");
	}

	@Override
	public String getSetSpec() {
		return setSpec;
	}

	public String getContentModel() {
		return contentModel;
	}

	@Override
	public String toString() {
		return "<set>" + "<setSpec>" + setSpec + "</setSpec>" + "<setName>"
				+ setName + "</setName>" + "</set>";
	}

}
