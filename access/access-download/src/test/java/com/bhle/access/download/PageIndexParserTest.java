package com.bhle.access.download;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.access.download.generator.PageIndexParser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PageIndexParserTest {
	@Test
	public void testSingleIndexParser(){
		String range = "2";
		String[] pageUris = new String[]{"info:fedora/bhle:10706-a000test-00001", "info:fedora/bhle:10706-a000test-00002"};
		String[] result = PageIndexParser.parse(range, pageUris);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[0]);

		range = "3";
		result = PageIndexParser.parse(range, pageUris);
		Assert.assertTrue(result.length == 0);
	}
	
	@Test
	public void testRangeIndexParser(){
		String range = "1-2";
		String[] pageUris = new String[]{"info:fedora/bhle:10706-a000test-00001", "info:fedora/bhle:10706-a000test-00002"};
		String[] result = PageIndexParser.parse(range, pageUris);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[1]);
		
		range = "0-3";
		result = PageIndexParser.parse(range, pageUris);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[1]);
		
		range = "2-3";
		result = PageIndexParser.parse(range, pageUris);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00002", result[0]);
		
		range = "0-1";
		result = PageIndexParser.parse(range, pageUris);
		Assert.assertEquals("info:fedora/bhle:10706-a000test-00001", result[0]);
	}
	
	
}
