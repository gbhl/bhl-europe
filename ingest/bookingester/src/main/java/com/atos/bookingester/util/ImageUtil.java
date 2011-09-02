package com.atos.bookingester.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;


public class ImageUtil {
	private static ConvertCmd convert = new ConvertCmd();
//	static {
//		convert.setSearchPath("C:\\Program Files\\ImageMagick-6.7.1-Q8");
//	}

	public static File resize(String imageUrl, int height, int width) {
		File destFile = null;

		try {
			File srcFile;
			srcFile = downloadImage(imageUrl);
			destFile = File.createTempFile("TNDEST", "JPG");

			IMOperation op = new IMOperation();
			op.thumbnail(width, height);
			op.addImage();
			op.addImage();

			convert.run(op, srcFile.toString(), "JPEG:" + destFile.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}
		return destFile;
	}

	public static File resizeToTN(String imageUrl) {
		return resize(imageUrl, 200, 200);
	}

	private static File downloadImage(String imageUrl) {
		File result = null;
		try {
			result = File.createTempFile("TNDOW", "IMG");
			FileOutputStream fos = new FileOutputStream(result);
			InputStream in = MyHttpClient.getAsStream(imageUrl);

			byte buf[] = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
				fos.write(buf, 0, len);
			fos.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static boolean isTIFF(String image) {
		String suffix = image.substring(image.lastIndexOf(".") + 1);
		return suffix.equalsIgnoreCase("tiff")
				|| suffix.equalsIgnoreCase("tif");
	}

	public static boolean isJP2(String image) {
		String suffix = image.substring(image.lastIndexOf(".") + 1);
		return suffix.equalsIgnoreCase("jpeg")
				|| suffix.equalsIgnoreCase("jpg");
	}

	public static File convertToJP2(String image) {
		File result = null;
		try {
			result = File.createTempFile("TNDST", "SRC");
			File tiffFile = downloadImage(image);

			IMOperation op = new IMOperation();
			op.quality(100.00);
			op.addImage();
			op.addImage();

			convert.run(op, tiffFile.toString(),
					"JP2:" + result.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}
		return result;
	}

}
