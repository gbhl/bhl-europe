package com.bhle.access.util;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SysInfo;

public class SystemManagementUtil {
	private static double LOAD_AVERAGE_LIMIT_PERCENTAGE;

	public static boolean isBusy() {
			return false;
		// int cpuCores = getTotalCpuCores();
		// return getTotalCpuCores() * LOAD_AVERAGE_LIMIT_PERCENTAGE < getLoadAverage();
	}

	private static int getTotalCpuCores() {
		try {
			Sigar sigar = new Sigar();
			CpuInfo[] cpuInfoList = sigar.getCpuInfoList();
			return cpuInfoList[0].getTotalCores();
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static double getLoadAverage() {
		Sigar sigar = new Sigar();
		try {
			return sigar.getLoadAverage()[0];
		} catch (SigarException e) {
			e.printStackTrace();
		}

		return -1;
	}
}
