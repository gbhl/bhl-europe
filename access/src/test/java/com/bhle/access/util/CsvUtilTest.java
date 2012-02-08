package com.bhle.access.util;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
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
public class CsvUtilTest implements ResourceLoaderAware {
	private ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private Resource pidCvs;

	@Before
	public void init() {
		pidCvs = resourceLoader
				.getResource("classpath:com/bhle/access/sample/pids.csv");
	}

	@Test
	public void testReadOneColumnCsv() {
		try {
			String csv = IOUtils.toString(pidCvs.getInputStream());
			String[] pids = CsvUtil.readOneColumnCsv(csv);
			Assert.assertTrue(pids.length == 114);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
