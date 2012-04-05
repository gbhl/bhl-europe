package com.bhle.access.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.bhle.access.convert.DatastreamConverter;

public class Derivative {
	private String pid;
	private String dsId;
	private InputStream inputStream;
	private DatastreamWrapper datastream;
	private List<DatastreamConverter> convertors = new ArrayList<DatastreamConverter>();

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

	public void addConvertor(DatastreamConverter convertor) {
		this.convertors.add(convertor);
	}

	public List<DatastreamConverter> getConvertors() {
		return convertors;
	}

	public DatastreamWrapper getDatastream() {
		return datastream;
	}

	public void setDatastream(DatastreamWrapper datastream) {
		this.datastream = datastream;
	}

	public void close() {
		datastream.close();
		IOUtils.closeQuietly(inputStream);
	}
}
