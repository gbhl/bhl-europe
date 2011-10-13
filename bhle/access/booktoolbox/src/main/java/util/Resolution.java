package util;

public class Resolution {
	private String resolution;
	public Resolution(String resolution) {
		super();
		this.resolution = resolution;
	}
	
	public int getLevel(){
		if (resolution.equalsIgnoreCase("high")){
			return 5;
		} else if (resolution.equalsIgnoreCase("low")){
			return 1;
		} else {
			return 3;
		}
	}
	
	public String getResolution(){
		return resolution;
	}
	
	
}
