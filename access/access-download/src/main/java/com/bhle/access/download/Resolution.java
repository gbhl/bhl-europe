package com.bhle.access.download;

import java.util.Map;

public class Resolution {
	private String resolution;
	private static Map<String, Integer> MAP;
	
	public void setMap(Map<String, Integer> map) {
		Resolution.MAP = map;
	}

	public Resolution() {
	}
	
	public Resolution(String resolution) {
		this.resolution = resolution;
	}

	public int getLevel(){
		return MAP.get(resolution);
	}

	public String getResolution() {
		return resolution;
	}
}
