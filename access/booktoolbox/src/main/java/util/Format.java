package util;

public enum Format {
	EMAIL("email"), PDF("pdf"), OCR("ocr"), JPEG("zip");

	private String format;

	private Format(String format) {
		this.format = format;
	}
	
	public String getFormatAsSuffix (){
		return format;
	}

	public static Format getFormat(String format) {
		if (format.equals("pdf")) {
			return Format.PDF;
		} else if (format.equals("ocr")) {
			return Format.OCR;
		} else if (format.equals("zip")) {
			return Format.JPEG;
		} else {
			return Format.EMAIL;
		}
	}
	
	public String getMIME(){
		if (format.equals("pdf")) {
			return "application/pdf";
		} else if (format.equals("ocr")) {
			return "text/plain";
		} else if (format.equals("zip")) {
			return "application/x-zip-compressed";
		} else {
			return "text/html";
		}
	}
}
