package util;

public enum Definition {
	HIGH(5), MEDIUM(3), LOW(1);

	private int level;

	private Definition (int level) {
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
}
