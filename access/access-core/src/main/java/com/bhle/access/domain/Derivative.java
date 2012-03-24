package com.bhle.access.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.bhle.access.convert.DatastreamConvertor;

public class Derivative {
	private String pid;
	private String dsId;
	private InputStream inputStream;
	private List<DatastreamConvertor> convertors = new ArrayList<DatastreamConvertor>();

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void addConvertor(DatastreamConvertor convertor) {
		this.convertors.add(convertor);
	}
	
	public List<DatastreamConvertor> getConvertors() {
		return convertors;
	}
	
	public void close(){
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
