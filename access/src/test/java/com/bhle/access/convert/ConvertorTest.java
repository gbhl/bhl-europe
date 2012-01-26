package com.bhle.access.convert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.im4java.process.ProcessStarter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.access.domain.DatastreamWrapper;
import com.bhle.access.domain.Derivative;
import com.bhle.access.domain.DigitalObjectWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ConvertorTest implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;

	}

	private DatastreamWrapper dcDatastreamWrapper = new DatastreamWrapper("DC");
	private DigitalObjectWrapper simpleObjectWrapper = new DigitalObjectWrapper(
			"test:SIMPLEOBJID");

	private DatastreamWrapper tiffDatastreamWrapper = new DatastreamWrapper(
			"TIFF");
	private DigitalObjectWrapper pageObjectWrapper = new DigitalObjectWrapper(
			"test:PAGEOBJECTID");

	@Before
	public void init() {
		String myPath = "C:\\Program Files\\ImageMagick";
		ProcessStarter.setGlobalSearchPath(myPath);
		
		initialSimpleObject();
		initialPageObject();
	}

	private void initialSimpleObject() {
		List<String> contentModels = new ArrayList<String>();
		contentModels.add("info:fedora/fedora-system:ContentModel-3.0");
		simpleObjectWrapper.setConternModels(contentModels);

		dcDatastreamWrapper.setMimeType("text/plain");
		dcDatastreamWrapper.setDigitalObject(simpleObjectWrapper);
		String dc = "<dc/>";
		dcDatastreamWrapper.setInputStream(new ByteArrayInputStream(dc
				.getBytes()));
	}

	private void initialPageObject() {
		List<String> contentModels = new ArrayList<String>();
		contentModels.add("info:fedora/bhle-cmodel:pageCModel");
		pageObjectWrapper.setConternModels(contentModels);

		tiffDatastreamWrapper.setMimeType("image/tiff");
		tiffDatastreamWrapper.setDigitalObject(pageObjectWrapper);
		try {
			tiffDatastreamWrapper.setInputStream(resourceLoader.getResource(
					"classpath:com/bhle/access/util/sample.TIF").getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTiff2JpegConvertor() {
		Tiff2JpegConvertor tiffConvertor = new Tiff2JpegConvertor();
		List<DatastreamConvertor> convertors = new ArrayList<DatastreamConvertor>();
		convertors.add(tiffConvertor);
		
		ConvertorManager.convertors = convertors;

		Derivative[] derivatives = ConvertorManager.derive(tiffDatastreamWrapper);
		Assert.assertEquals(1, derivatives.length);

		File jp2 = null;
		try {
			jp2 = File.createTempFile("TEST", null);
			jp2.deleteOnExit();
			FileOutputStream fout = new FileOutputStream(jp2);
			InputStream in = derivatives[0].getInputStream();
			byte[] buffer = new byte[1024];
			while (in.read(buffer) != -1) {
				fout.write(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Info info = new Info(jp2.getAbsolutePath(), true);
			Assert.assertEquals("JP2", info.getImageFormat());
		} catch (InfoException e) {
			e.printStackTrace();
		}
	}
}
