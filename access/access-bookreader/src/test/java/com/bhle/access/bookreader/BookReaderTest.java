package com.bhle.access.bookreader;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.access.bookreader.search.SearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BookReaderTest {
	
	@Ignore
	@Test
	public void testSearch(){
		String result = SearchService.query("a00000000000132805961115", "Standley");
		System.out.println(result);
	}
}
