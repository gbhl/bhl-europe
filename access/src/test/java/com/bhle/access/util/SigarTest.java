package com.bhle.access.util;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.junit.Test;

public class SigarTest {
	@Test
	public void testSigarBasic(){
		System.out.println(Sigar.VERSION_STRING);
		Sigar sigar = new Sigar();
		try {
			System.out.println(sigar.getCpu().getTotal());
		} catch (SigarException e) {
			e.printStackTrace();
		}
	}
}
