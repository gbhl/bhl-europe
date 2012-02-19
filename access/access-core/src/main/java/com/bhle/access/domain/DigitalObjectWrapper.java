package com.bhle.access.domain;

import java.util.ArrayList;
import java.util.List;

public class DigitalObjectWrapper {
	private String pid;
	private List<String> conternModels;
	private List<DatastreamWrapper> datastreams;

	public DigitalObjectWrapper(String pid) {
		this.pid = pid;
		datastreams = new ArrayList<DatastreamWrapper>();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public List<String> getConternModels() {
		return conternModels;
	}

	public void setConternModels(List<String> conternModels) {
		this.conternModels = conternModels;
	}

	public List<DatastreamWrapper> getDatastreams() {
		return datastreams;
	}

	public void setDatastreams(List<DatastreamWrapper> datastreams) {
		this.datastreams = datastreams;
	}
	
	public void addDatastreams(DatastreamWrapper datastream) {
		this.datastreams.add(datastream);
	}

	public boolean hasModel(String[] targetContentModels) {
		for (String contentModel : targetContentModels) {
			if (conternModels.contains(contentModel)) {
				return true;
			}
		}
		return false;
	}

}
