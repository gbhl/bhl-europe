package com.bhle.access.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

public class BibUtils {

	private static Resource LINUX_XML2BIB;
	private static Resource LINUX_XML2END;
	private static Resource WIN_XML2BIB;
	private static Resource WIN_XML2END;

	private static String XML2BIB_PATH;
	private static String XML2END_PATH;

	@PostConstruct
	public void init() {
		if (OSValidator.isWindows()) {
			try {
				XML2BIB_PATH = WIN_XML2BIB.getFile().getAbsolutePath();
				XML2END_PATH = WIN_XML2END.getFile().getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (OSValidator.isUnix()) {
			try {
				XML2BIB_PATH = LINUX_XML2BIB.getFile().getAbsolutePath();
				XML2END_PATH = LINUX_XML2END.getFile().getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				throw new UnsupportedOperationSystem();
			} catch (UnsupportedOperationSystem e) {
				e.printStackTrace();
			}
		}
	}

	@Autowired
	public void setLINUX_XML2BIB(Resource lINUX_XML2BIB) {
		LINUX_XML2BIB = lINUX_XML2BIB;
	}

	@Autowired
	public void setLINUX_XML2END(Resource lINUX_XML2END) {
		LINUX_XML2END = lINUX_XML2END;
	}

	@Autowired
	public void setWIN_XML2BIB(Resource wIN_XML2BIB) {
		WIN_XML2BIB = wIN_XML2BIB;
	}

	@Autowired
	public void setWIN_XML2END(Resource wIN_XML2END) {
		WIN_XML2END = wIN_XML2END;
	}

	public static InputStream mods2BibTex(InputStream modsInputStream) {
		try {
			File tmp = copyInputStreamToTmpFile(modsInputStream);
			ProcessBuilder pb;
			if (OSValidator.isWindows()) {
				pb = new ProcessBuilder("cmd.exe", "/C",
						XML2BIB_PATH, tmp.getAbsolutePath());
			} else {
				pb = new ProcessBuilder(XML2BIB_PATH, tmp.getAbsolutePath());
			}
			Process process = pb.start();
			return process.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream mods2Endnote(InputStream modsInputStream) {
		try {
			File tmp = copyInputStreamToTmpFile(modsInputStream);
			ProcessBuilder pb;
			if (OSValidator.isWindows()) {
				pb = new ProcessBuilder("cmd.exe", "/C",
						XML2END_PATH, tmp.getAbsolutePath());
			} else {
				pb = new ProcessBuilder(XML2END_PATH, tmp.getAbsolutePath());
			}
			Process process = pb.start();
			return process.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static File copyInputStreamToTmpFile(InputStream modsInputStream)
			throws IOException {
		File tmp = File.createTempFile("bibutils", null);
		tmp.deleteOnExit();
		FileWriter fw = new FileWriter(tmp);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				modsInputStream));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				fw.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		fw.close();
		return tmp;
	}
}
