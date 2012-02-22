package com.bhle.access.bookreader.search;

public class Box {
	public Box(String line) {
		String[] pars = line.split(" ");
		this.text = pars[0];
		this.x_left = Integer.valueOf(pars[1]);
		this.y_bottom = Integer.valueOf(pars[2]);
		this.x_right = Integer.valueOf(pars[3]);
		this.y_top = Integer.valueOf(pars[4]);
	}

	public Box(String text, int x_left, int y_bottom, int x_right, int y_top) {
		this.text = text;
		this.x_left = x_left;
		this.y_bottom = y_bottom;
		this.x_right = x_right;
		this.y_top = y_top;
	}

	private int x_left;
	private int y_bottom;
	private int x_right;
	private int y_top;
	private String text;
	private int page;

	public int getLeft() {
		return x_left;
	}

	// Tesseract only provide Bottom, rather than Top
	public int getBottom() {
		return y_bottom;
	}

	public int getHeight() {
		return y_top - y_bottom;
	}

	public int getWidth() {
		return x_right - x_left;
	}

	public String getText() {
		return this.text;
	}

	public static Box merge(Box... boxes) {
		StringBuffer text = new StringBuffer();
		int x_left = Integer.MAX_VALUE;
		int y_bottom = Integer.MAX_VALUE;
		int x_right = Integer.MIN_VALUE;
		int y_top = Integer.MIN_VALUE;

		for (Box box : boxes) {
			text.append(box.getText());
			x_left = Math.min(x_left, box.x_left);
			y_bottom = Math.min(y_bottom, box.y_bottom);
			x_right = Math.max(x_right, box.x_right);
			y_top = Math.max(y_top, box.y_top);
			;
		}

		return new Box(text.toString(), x_left, y_bottom, x_right, y_top);
	}

	@Override
	public String toString() {
		return text;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

}
