package com.bhle.access.oai;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import proai.MetadataFormat;
import proai.SetInfo;

public class MetadataFormatProviderTest {

	private MetadataFormatProvider metadataFormatProvider;

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
		metadataFormatProvider = new MetadataFormatProviderImpl(props);
	}

	@Test
	public void testListSetInfo() {
		for (MetadataFormat md : metadataFormatProvider.getMetadataFormatCollection()) {
			System.out.println(md);
		}

	}
}
