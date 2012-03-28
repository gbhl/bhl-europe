package com.bhle.access.oai;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import proai.SetInfo;

public class SetInfoProviderTest {

	private SetInfoPrivoder setInfoPrivoder;

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
		setInfoPrivoder = new SetInfoProviderImpl(props);
	}

	@Test
	public void testListSetInfo() {
		for (SetInfo info : setInfoPrivoder.getSetInfoCollection()) {
			System.out.println(info);
		}

	}
}
