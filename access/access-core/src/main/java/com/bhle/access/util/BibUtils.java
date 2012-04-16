package com.bhle.access.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
		if (OSValidator.isWindows()) {
			try {
				File bib = copyInputStreamToTmpFile(
						WIN_XML2BIB.getInputStream(), ".exe");
				XML2BIB_PATH = bib.getAbsolutePath();
				File end = copyInputStreamToTmpFile(
						WIN_XML2END.getInputStream(), ".exe");
				XML2END_PATH = end.getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (OSValidator.isUnix()) {
			try {
				File bib = copyInputStreamToTmpFile(
						LINUX_XML2BIB.getInputStream(), "");
				XML2BIB_PATH = bib.getAbsolutePath();
				 bib.setExecutable(true, false);
				File end = copyInputStreamToTmpFile(
						LINUX_XML2END.getInputStream(), "");
				XML2END_PATH = end.getAbsolutePath();
				 end.setExecutable(true, false);
			} catch (IOException e) {
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
		tmp.deleteOnExit();
		FileOutputStream out = new FileOutputStream(tmp);
		IOUtils.copy(in, out);
		out.close();
		return tmp;
	}
}
