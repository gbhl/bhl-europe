package com.bhle.access.oai;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import proai.Record;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RecordProviderTest {

	private RecordProvider recordProvider;

	@Before
	public void init() {
		InputStream propStream = this.getClass().getResourceAsStream(
				"/proai.properties");
		Properties props = new Properties();
		try {
			props.load(propStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SetInfoPrivoder setInfoPrivoder = new SetInfoProviderImpl(props);
		MetadataFormatProvider metadataFormatProvider = new MetadataFormatProviderImpl(props);
		recordProvider = new RecordProviderImpl(setInfoPrivoder, metadataFormatProvider);
	}
	
	@Test
	public void testListESERecords(){
		 Collection<Record> records = recordProvider.getRecordCollection(new Date(0), new Date(), "mods");
		 for (Record record : records) {
			System.out.println(record);
		}
	}
}
