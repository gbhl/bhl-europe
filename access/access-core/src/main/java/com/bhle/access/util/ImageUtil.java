package com.bhle.access.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(ImageUtil.class);

	private static int DEFAULT_MAX_HEIGHT = 1920;
	private static int DEFAULT_MAX_WIDTH = 1920;

	public static InputStream tiffToJp2(InputStream tiffIn, int maxHeight,
			int maxWidth) {
		File tmpTiff = copyToTempFile(tiffIn);

		File tmpJp2 = null;
		try {
			tmpJp2 = File.createTempFile("derivative_image", ".jp2");
			tmpJp2.deleteOnExit();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		IMOperation op = new IMOperation();
		op.addImage(tmpTiff.getAbsolutePath());
		op.resize(maxHeight, maxWidth);
		op.addImage(tmpJp2.getAbsolutePath());

		ConvertCmd convert = new ConvertCmd();
		try {
			convert.run(op);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}

		ByteArrayInputStream byteIn = null;
		try {
			FileInputStream fileIn = new FileInputStream(tmpJp2);
			byteIn = new ByteArrayInputStream(IOUtils.toByteArray(fileIn));
			fileIn.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			tiffIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!tmpTiff.delete() && tmpTiff.exists()) {
			logger.warn("Cannot delete temp files {}", tmpTiff);
		}

		if (!tmpJp2.delete() && tmpJp2.exists()) {
			logger.warn("Cannot delete temp files {}", tmpJp2);
		}

		return byteIn;
	}

	public static InputStream tiffToJp2(InputStream tiffIn) {
		return tiffToJp2(tiffIn, DEFAULT_MAX_HEIGHT, DEFAULT_MAX_WIDTH);
	}

	public static InputStream jp2ToThumbnail(InputStream jp2In) {
		File tmpJp2 = copyToTempFile(jp2In);

		File tmpTn = null;
		try {
			tmpTn = File.createTempFile("derivative_image", ".jpeg");
			tmpTn.deleteOnExit();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		IMOperation op = new IMOperation();
		op.thumbnail(150);
		op.addImage(tmpJp2.getAbsolutePath());
		op.addImage(tmpTn.getAbsolutePath());

		ConvertCmd convert = new ConvertCmd();
		try {
			convert.run(op);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}

		ByteArrayInputStream byteIn = null;
		try {
			FileInputStream fileIn = new FileInputStream(tmpTn);
			byteIn = new ByteArrayInputStream(IOUtils.toByteArray(fileIn));
			fileIn.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			jp2In.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!tmpJp2.delete() && tmpJp2.exists()) {
			logger.warn("Cannot delete temp files {}", tmpJp2);
		}

		if (!tmpTn.delete() && tmpTn.exists()) {
			logger.warn("Cannot delete temp files {}", tmpTn);
		}

		return byteIn;
	}

	public static int[] tiffToJp2Size(String path) {
		int height = 0;
		int width = 0;
		try {
			Info info = new Info(path, true);

			int tiffHeight = info.getImageHeight();
			height = tiffHeight;
			int tiffWidth = info.getImageWidth();
			width = tiffWidth;
			if (tiffHeight > DEFAULT_MAX_HEIGHT
					|| tiffWidth > DEFAULT_MAX_WIDTH) {
				if (tiffHeight > tiffWidth) {
					double ratio = tiffHeight / DEFAULT_MAX_HEIGHT;
					height = DEFAULT_MAX_HEIGHT;
					width = (int) (tiffWidth / ratio);
				} else {
					double ratio = tiffWidth / DEFAULT_MAX_WIDTH;
					height = (int) (tiffHeight / ratio);
					width = DEFAULT_MAX_WIDTH;
				}
			}
		} catch (InfoException e) {
			e.printStackTrace();
		}
		return new int[] { height, width };
	}

	public static int[] tiffToJp2Size(InputStream tiffIn) {
		try {
			File tmpFile = File.createTempFile("temp", "tiff");
			IOUtils.copy(tiffIn, new FileOutputStream(tmpFile));
			tiffIn.close();
			return tiffToJp2Size(tmpFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new int[] { 0, 0 };
	}

	private static File copyToTempFile(InputStream in) {
		try {
			File tmp = File.createTempFile("derivative_image", null);
			FileOutputStream fileOut = new FileOutputStream(tmp);
			IOUtils.copy(in, fileOut);
			fileOut.close();
			return tmp;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
