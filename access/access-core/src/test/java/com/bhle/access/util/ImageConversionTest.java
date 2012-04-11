package com.bhle.access.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.process.Pipe;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.core.util.FileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ImageConversionTest implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Test
	public void testTiffToJp2() throws Exception {
		Resource tiff = resourceLoader
				.getResource("classpath:com/bhle/access/sample/sample.TIF");

		InputStream jp2In = ImageUtil.tiffToJp2(tiff.getInputStream());

		File jp2 = File.createTempFile("test", ".jp2");
		Assert.assertTrue(jp2.exists());

		FileOutputStream jp2Out = new FileOutputStream(jp2);

		IOUtils.copy(jp2In, jp2Out);

		Info info = new Info(jp2.getAbsolutePath(), true);
		Assert.assertEquals("JP2", info.getImageFormat());

		jp2.delete();
	}

	@Ignore
	@Test
	public void testPiping() throws IOException, InterruptedException,
			IM4JavaException {
		Resource tiff = resourceLoader
				.getResource("classpath:com/bhle/access/sample/sample.TIF");

		String currentPath = tiff.getFile().getParent();
		String tiffPath = tiff.getFile().getAbsolutePath();
		String jp2Path = currentPath + File.separator + "sample.jp2";

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		IMOperation op = new IMOperation();
		op.addImage(tiffPath);
		op.addImage("jp2:-");

		Pipe pipe = new Pipe(null, byteArrayOutputStream);

		ConvertCmd convert = new ConvertCmd();
		convert.setOutputConsumer(pipe);
		convert.run(op);

		OutputStream out = new FileOutputStream(jp2Path);
		out.write(byteArrayOutputStream.toByteArray());

		byteArrayOutputStream.close();
		out.close();
	}
}
