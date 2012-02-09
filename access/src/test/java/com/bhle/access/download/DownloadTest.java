package com.bhle.access.download;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DownloadTest {
	@Ignore
	@Test
	public void testPidExtractor(){
		PageURIExtractor extractor = new PageURIExtractorImpl();
		
		String[] pageURIs = null;
		
		pageURIs = extractor.getPageURIs("a0hhmgs3", "n2");
		Assert.assertEquals(1, pageURIs.length);
		Assert.assertEquals("info:fedora/bhle:a0hhmgs3-00002", pageURIs[0]);
		
		pageURIs = extractor.getPageURIs("a0hhmgs3", "n2-n3");
		Assert.assertEquals(1, pageURIs.length);
		Assert.assertEquals("info:fedora/bhle:a0hhmgs3-00002", pageURIs[0]);
	}
}
