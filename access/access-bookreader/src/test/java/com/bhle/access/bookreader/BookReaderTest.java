package com.bhle.access.bookreader;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BookReaderTest {

	@Ignore
	@Test
	public void testBuildBookInfo() {
		BookInfo book = BookInfoBuilder.build("a00000000000132805961115");
		Assert.assertNotNull(book.getTitle());
		Assert.assertNotNull(book.getEntryPageIndex());
	}
}
