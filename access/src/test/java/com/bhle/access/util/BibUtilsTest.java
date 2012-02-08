package com.bhle.access.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BibUtilsTest implements ResourceLoaderAware {
	private ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private Resource mods;

	@Before
	public void init() {
		mods = resourceLoader
				.getResource("classpath:com/bhle/access/sample/MODS.xml");
	}

	@Test
	public void testModsToBib(){
		InputStream in;
		try {
			in = BibUtils.mods2BibTex(mods.getInputStream());
			IOUtils.copy(in, System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testModsToEnd(){
		InputStream in;
		try {
			in = BibUtils.mods2Endnote(mods.getInputStream());
			IOUtils.copy(in, System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
