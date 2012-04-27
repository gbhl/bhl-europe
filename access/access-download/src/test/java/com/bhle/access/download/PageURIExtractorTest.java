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
	public void testExtractingSingeIndex() {
		String rangesParameter = "0,1";
		String[] pageUris = new String[] {
				"info:fedora/bhle:10706-a000test-00001",
				"info:fedora/bhle:10706-a000test-00002" };
		String[] result = extractor.getPageURIs(rangesParameter, pageUris);
		Assert.assertEquals(2, result.length);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[1]);
		
		rangesParameter = "0";
		pageUris = new String[] {
				"info:fedora/bhle:10706-a000test-00001",
				"info:fedora/bhle:10706-a000test-00002" };
		result = extractor.getPageURIs(rangesParameter, pageUris);
		Assert.assertEquals(1, result.length);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
	}

	@Test
	public void testExtractingRanges() {
		String rangesParameter = "0-0,0-1";
		String[] pageUris = new String[] {
				"info:fedora/bhle:10706-a000test-00001",
				"info:fedora/bhle:10706-a000test-00002" };
		String[] result = extractor.getPageURIs(rangesParameter, pageUris);
		Assert.assertEquals(3, result.length);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[1]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[2]);

		rangesParameter = "";
		pageUris = new String[] { "info:fedora/bhle:10706-a000test-00001",
				"info:fedora/bhle:10706-a000test-00002" };
		result = extractor.getPageURIs(rangesParameter, pageUris);
		Assert.assertEquals(2, result.length);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[1]);
	}
}
