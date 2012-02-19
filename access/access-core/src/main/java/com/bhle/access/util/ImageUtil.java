package com.bhle.access.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

		Pipe pipeIn = new Pipe(tiffIn, null);
		Pipe pipeOut = new Pipe(null, byteOut);

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

		ByteArrayInputStream byteIn = new ByteArrayInputStream(
				byteOut.toByteArray());

		try {
			byteOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteIn;
	}

	public static InputStream tiffToJp2(InputStream tiffIn) {
		return tiffToJp2(tiffIn, DEFAULT_MAX_HEIGHT, DEFAULT_MAX_WIDTH);
	}
	
	public static InputStream jp2ToThumbnail(InputStream jp2In) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

		Pipe pipeIn = new Pipe(jp2In, null);
		Pipe pipeOut = new Pipe(null, byteOut);

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

		ByteArrayInputStream byteIn = new ByteArrayInputStream(
				byteOut.toByteArray());

		try {
			byteOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteIn;
	}

	public static InputStream tiffToJp2Size(InputStream tiffIn) {
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
			if (tiffHeight > DEFAULT_MAX_HEIGHT || tiffWidth > DEFAULT_MAX_WIDTH){
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
			
			tmpFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InfoException e) {
			e.printStackTrace();
		}
		return IOUtils.toInputStream(height + "," + width);
	}
	
	
}
