package com.bhle.access.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
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
		String suffix = null;
		if (OSValidator.isWindows()) {
			suffix = ".exe";
		} else if (OSValidator.isUnix()) {
			suffix = ".sh";
		}
		try {
			File bib = copyInputStreamToTmpFile(WIN_XML2BIB.getInputStream(),
					suffix);
			XML2BIB_PATH = bib.getAbsolutePath();
//			bib.setExecutable(true, false);
			File end = copyInputStreamToTmpFile(WIN_XML2END.getInputStream(),
					suffix);
			XML2END_PATH = end.getAbsolutePath();
//			end.setExecutable(true, false);
		} catch (IOException e) {
			e.printStackTrace();
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
			File tmp = copyInputStreamToTmpFile(modsInputStream, null);
			ProcessBuilder pb;
			if (OSValidator.isWindows()) {
				pb = new ProcessBuilder("cmd.exe", "/C", XML2BIB_PATH,
						tmp.getAbsolutePath());
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
			File tmp = copyInputStreamToTmpFile(modsInputStream, null);
			ProcessBuilder pb;
			if (OSValidator.isWindows()) {
				pb = new ProcessBuilder("cmd.exe", "/C", XML2END_PATH,
						tmp.getAbsolutePath());
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

	private static File copyInputStreamToTmpFile(InputStream in, String suffix)
			throws IOException {
		File tmp = File.createTempFile("bibutils", suffix);
//		tmp.deleteOnExit();
		FileOutputStream out = new FileOutputStream(tmp);
		IOUtils.copy(in, out);
		out.close();
		return tmp;
	}
}
