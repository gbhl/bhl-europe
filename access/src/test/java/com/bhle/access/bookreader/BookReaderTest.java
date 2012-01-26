package com.bhle.access.bookreader;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BookReaderTest {
	
	@Ignore
	@Test
	public void testBuildBookInfo(){
		BookInfo book = BookInfoBuilder.build("a0hhmgs3");
		Assert.assertNotNull(book.getTitle());
		Assert.assertNotNull(book.getEntryPageIndex());
	}
}
