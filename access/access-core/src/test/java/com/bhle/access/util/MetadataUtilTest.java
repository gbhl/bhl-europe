package com.bhle.access.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MetadataUtilTest implements ResourceLoaderAware {

	private static final Logger logger = LoggerFactory
			.getLogger(MetadataUtilTest.class);

	private ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private Resource olef;

	@Before
	public void init() {
		olef = resourceLoader
				.getResource("classpath:com/bhle/access/sample/OLEF.xml");
	}

	@Test
	public void testOlef2Dc() {
		try {
			InputStream in = MetadataUtil.olefToDc(olef.getInputStream());
			String dc = IOUtils.toString(in);
			logger.debug(dc);
			Assert.assertNotNull(dc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOlef2Mods() {
		try {
			InputStream in = MetadataUtil.olefToMods(olef.getInputStream());
			String mods = IOUtils.toString(in);
			logger.debug(mods);
			Assert.assertNotNull(mods);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOlef2Ese() {
		try {
			InputStream in = MetadataUtil.olefToEse(olef.getInputStream());
			String ese = IOUtils.toString(in);
			logger.debug(ese);
			Assert.assertNotNull(ese);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testOlef2Marc21() {
		try {
			InputStream in = MetadataUtil.olefToMarcxml(olef.getInputStream());
			String marc21 = IOUtils.toString(in);
			logger.debug(marc21);
			Assert.assertNotNull(marc21);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOlef2BibTex() {
		try {
			InputStream in = MetadataUtil.olefToBibText(olef.getInputStream());
			String bib = IOUtils.toString(in);
			logger.debug(bib);
			Assert.assertNotNull(bib);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOlef2Endnote() {
		try {
			InputStream in = MetadataUtil.olefToEndnote(olef.getInputStream());
			String end = IOUtils.toString(in);
			logger.debug(end);
			Assert.assertNotNull(end);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetOlefTitle() {
		try {
			Olef olefDoc = new Olef(olef.getInputStream());
			String title = olefDoc.getTitle();
			Assert.assertEquals(
					"vegetation of Caithness considered in relation to the geology",
					title);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetOlefEntryPage() {
		try {
			Olef olefDoc = new Olef(olef.getInputStream());
			String entryPage = olefDoc.getEntryPage();
			Assert.assertEquals("2", entryPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOlefScientificNames() {
		try {
			Olef olefDoc = new Olef(olef.getInputStream());
			List<String> names = olefDoc.getScientificNames(6);
			for (String name : names) {
				logger.debug(name);
			}
			Assert.assertEquals(1, names.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
