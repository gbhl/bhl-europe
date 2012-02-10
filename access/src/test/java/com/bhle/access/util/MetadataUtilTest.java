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
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MetadataUtilTest implements ResourceLoaderAware {
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
			print(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOlef2Mods() {
		try {
			InputStream in = MetadataUtil.olefToMods(olef.getInputStream());
			print(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testOlef2Marc21() {
		try {
			InputStream in = MetadataUtil.olefToMarc21(olef.getInputStream());
			print(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testOlef2BibTex() {
		try {
			InputStream in = MetadataUtil.olefToBibText(olef.getInputStream());
			print(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testOlef2Endnote() {
		try {
			InputStream in = MetadataUtil.olefToEndnote(olef.getInputStream());
			print(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetOlefTitle() {
		try {
			Olef olefDoc = new Olef(olef.getInputStream());
			String title = olefDoc.getTitle();
			Assert.assertEquals("Billeder af Nordens flora", title);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetOlefEntryPage() {
		try {
			Olef olefDoc = new Olef(olef.getInputStream());
			String entryPage = olefDoc.getEntryPage();
			Assert.assertEquals("18", entryPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOlefScientificNames(){
		try {
			Olef olefDoc = new Olef(olef.getInputStream());
			List<String> names = olefDoc.getScientificNames(8);
			Assert.assertEquals(39, names.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void print(InputStream in) {
		try {
			IOUtils.copy(in, System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
