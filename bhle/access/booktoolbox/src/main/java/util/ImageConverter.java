package util;


import com.itextpdf.text.Image;

public interface ImageConverter {
	public Image convert(String location);

	public Image convertFromSource(String source, Resolution resolution);
	
	public Image convertFromPID(String pid, Resolution resolution);
}
