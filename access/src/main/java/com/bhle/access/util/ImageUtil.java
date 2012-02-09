package com.bhle.access.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.kahadb.util.ByteArrayInputStream;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;
import org.im4java.process.ProcessStarter;

public class ImageUtil {

	private static int DEFAULT_MAX_HEIGHT = 1920;
	private static int DEFAULT_MAX_WEIGHT = 1920;

	static {
		String myPath = "C:\\Program Files\\ImageMagick";
		ProcessStarter.setGlobalSearchPath(myPath);
	}

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
		return tiffToJp2(tiffIn, DEFAULT_MAX_HEIGHT, DEFAULT_MAX_WEIGHT);
	}

}
