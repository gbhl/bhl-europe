package com.bhle.access.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

	private DatastreamWrapper tiffDatastreamWrapper = new DatastreamWrapper(
			"TIFF");
	private DigitalObjectWrapper pageObjectWrapper = new DigitalObjectWrapper(
			"test:PAGEOBJECTID");

	private DatastreamWrapper olefDatastreamWrapper = new DatastreamWrapper(
			"OLEF");
	private DigitalObjectWrapper bookObjectWrapper = new DigitalObjectWrapper(
			"test:BOOKOBJECTID");

	@Before
	public void init() {
		initialBookObject();
		initialPageObject();
	}

	private void initialBookObject() {
		List<String> contentModels = new ArrayList<String>();
		contentModels.add("info:fedora/bhle-cmodel:monographCModel");
		bookObjectWrapper.setConternModels(contentModels);

		olefDatastreamWrapper.setMimeType("text/xml");
		olefDatastreamWrapper.setDigitalObject(bookObjectWrapper);
		Resource olef = resourceLoader
				.getResource("classpath:com/bhle/access/sample/OLEF.xml");
		try {
			olefDatastreamWrapper.setInputStream(olef.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initialPageObject() {
		List<String> contentModels = new ArrayList<String>();
		contentModels.add("info:fedora/bhle-cmodel:pageCModel");
		pageObjectWrapper.setConternModels(contentModels);

		tiffDatastreamWrapper.setMimeType("image/tiff");
		tiffDatastreamWrapper.setDigitalObject(pageObjectWrapper);
		try {
			tiffDatastreamWrapper.setInputStream(resourceLoader.getResource(
					"classpath:com/bhle/access/sample/sample.TIF")
					.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testTiff2JpegConvertor() {
		Tiff2JpegConverter tiffConvertor = new Tiff2JpegConverter();
		List<DatastreamConverter> convertors = new ArrayList<DatastreamConverter>();
		convertors.add(tiffConvertor);

		ConverterManager.converters = convertors;

		Derivative[] derivatives = ConverterManager
				.derive(tiffDatastreamWrapper);
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

	@Ignore
	@Test
	public void testOlefConvertors() {
		Olef2DcConverter olef2DcConvertor = new Olef2DcConverter();
		Olef2Marc21Converter olef2Marc21Convertor = new Olef2Marc21Converter();
		Olef2ModsConverter olef2ModsConvertor = new Olef2ModsConverter();
		Olef2OlefConverter olef2OlefConvertor = new Olef2OlefConverter();

		List<DatastreamConverter> convertors = new ArrayList<DatastreamConverter>();
		convertors.add(olef2DcConvertor);
		convertors.add(olef2Marc21Convertor);
		convertors.add(olef2ModsConvertor);
		convertors.add(olef2OlefConvertor);

		ConverterManager.converters = convertors;

		Derivative[] derivatives = ConverterManager
				.derive(olefDatastreamWrapper);

		Assert.assertEquals(4, derivatives.length);
	}
}
