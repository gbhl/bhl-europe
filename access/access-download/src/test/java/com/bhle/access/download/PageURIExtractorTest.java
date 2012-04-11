package com.bhle.access.download;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.access.download.generator.PageURIExtractorImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PageURIExtractorTest {
	
	private PageURIExtractorImpl extractor = new PageURIExtractorImpl();

	@Test
	public void testExtractingSingeIndex(){
		String rangesParameter = "1,2";
		String[] pageUris = new String[]{"info:fedora/bhle:10706-a000test-00001", "info:fedora/bhle:10706-a000test-00002"};
		String[] result = extractor.getPageURIs(rangesParameter, pageUris);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[1]);
	}
	
	@Test
	public void testExtractingRanges(){
		String rangesParameter = "1-1,1-2";
		String[] pageUris = new String[]{"info:fedora/bhle:10706-a000test-00001", "info:fedora/bhle:10706-a000test-00002"};
		String[] result = extractor.getPageURIs(rangesParameter, pageUris);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[1]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[2]);
	}
}
