import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.codec.TiffImage;

public class Test {
	public static void main(String[] args) {
		try {
			for (int i = 0; i < 200; i++) {
				Image tiff = Image
						.getInstance(new URL(
								"http://localhost:8080/fedora/objects/demo%3Adarwin-001/datastreams/TIFF/content"));
				System.out.println(tiff.getWidth());
				System.out.println(tiff.getHeight());

			}

		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
