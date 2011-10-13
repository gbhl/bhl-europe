package util;

public class Range {
	private int lower;
	private int upper;

	public Range(int lower, int upper) {
		super();
		this.lower = lower;
		this.upper = upper;
	}
	
	public Range(String rangeString){
		String[] indice = rangeString.split("-");
		if (indice.length == 1){
			this.lower = Integer.valueOf(indice[0]);
			this.upper = Integer.valueOf(indice[0]);
		} else {
			this.lower = Integer.valueOf(indice[0]);
			this.upper = Integer.valueOf(indice[1]);
		}
	}

	public int getLower() {
		return lower;
	}

	public int getUpper() {
		return upper;
	}
	
	
	
	public void setLower(int lower) {
		this.lower = lower;
	}

	public void setUpper(int upper) {
		this.upper = upper;
	}

	public boolean isValid(){
		return lower <= upper;
	}

	@Override
	public String toString() {
		return lower + "-" + upper;
	}
}
