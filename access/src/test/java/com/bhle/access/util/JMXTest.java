package com.bhle.access.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.junit.Test;

import com.ibm.icu.impl.Assert;

public class JMXTest {
	@Test
	public void testGetLoadAverage() {
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory
				.getOperatingSystemMXBean();
		double loadAverage = operatingSystemMXBean.getSystemLoadAverage();
		System.out.println(loadAverage);
		Assert.assrt(loadAverage > 0);
	}
}
