package util;

public class Resolution {
	private String resolution;
	public Resolution(String resolution) {
		super();
		this.resolution = resolution;
	}
	
	public int getLevel(){
		if (resolution.equalsIgnoreCase("high")){
			return GlobalParameter.LEVEL_HIGH_RESOLUTION;
		} else if (resolution.equalsIgnoreCase("low")){
			return GlobalParameter.LEVEL_LOW_RESOLUTION;
		} else {
			return GlobalParameter.LEVEL_MEDIUM_RESOLUTION;
		}
	}
	
	public String getResolution(){
		return resolution;
	}
	
	
}
