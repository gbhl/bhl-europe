package com.bhle.access.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.kahadb.util.ByteArrayInputStream;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.im4java.process.Pipe;
import org.im4java.process.ProcessStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(ImageUtil.class);

	private static int DEFAULT_MAX_HEIGHT = 1920;
	private static int DEFAULT_MAX_WIDTH = 1920;

	public static InputStream tiffToJp2(InputStream tiffIn, int maxHeight,
			int maxWidth) {
		File tmp = null;
		FileOutputStream fileOut = null;
		try {
			tmp = File.createTempFile("bhle", ".jp2");
			tmp.deleteOnExit();
			fileOut = new FileOutputStream(tmp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Pipe pipeIn = new Pipe(tiffIn, null);
		Pipe pipeOut = new Pipe(null, fileOut);

		IMOperation op = new IMOperation();
		op.addImage("-");
		op.resize(maxHeight, maxWidth);
		op.addImage("jp2:-");

		ConvertCmd convert = new ConvertCmd();
		convert.setInputProvider(pipeIn);
		convert.setOutputConsumer(pipeOut);
		// convert.setAsyncMode(true);
		try {
			convert.run(op);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}

		FileInputStream fileIn = null;
		try {
			fileIn = new FileInputStream(tmp);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			tiffIn.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileIn;
	}

	public static InputStream tiffToJp2(InputStream tiffIn) {
		return tiffToJp2(tiffIn, DEFAULT_MAX_HEIGHT, DEFAULT_MAX_WIDTH);
	}

	public static InputStream jp2ToThumbnail(InputStream jp2In) {
		File tmp = null;
		FileOutputStream fileOut = null;
		try {
			tmp = File.createTempFile("bhle", ".jpeg");
			tmp.deleteOnExit();
			fileOut = new FileOutputStream(tmp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Pipe pipeIn = new Pipe(jp2In, null);
		Pipe pipeOut = new Pipe(null, fileOut);

		IMOperation op = new IMOperation();
		op.thumbnail(150);
		op.addImage("-");
		op.addImage("jpeg:-");

		ConvertCmd convert = new ConvertCmd();
		convert.setInputProvider(pipeIn);
		convert.setOutputConsumer(pipeOut);
		// convert.setAsyncMode(true);
		try {
			convert.run(op);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}

		FileInputStream fileIn = null;
		try {
			fileIn = new FileInputStream(tmp);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			jp2In.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileIn;
	}

	public static int[] tiffToJp2Size(InputStream tiffIn) {
		int height = 0;
		int width = 0;
		try {
			File tmpFile = File.createTempFile("temp", "tiff");
			IOUtils.copy(tiffIn, new FileOutputStream(tmpFile));
			Info info = new Info(tmpFile.getAbsolutePath(), true);

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

			tiffIn.close();
			tmpFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InfoException e) {
			e.printStackTrace();
		}
		return new int[] { height, width };
	}

}
