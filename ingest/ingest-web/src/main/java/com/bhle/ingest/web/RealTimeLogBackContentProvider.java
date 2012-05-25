package com.bhle.ingest.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RealTimeLogBackContentProvider implements ContentProvider {

	// can be configure in batch-default.properties
	private String logFilePath;

	// record the ending position of file reading from last time
	private long oldFileSize;

	RandomAccessFile randomFile;

	public boolean openFile() {

		try {
			File logFile = new File(logFilePath);
			if (!logFile.exists())
				return false;
			randomFile = new RandomAccessFile(logFile, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public String getContent() {

		String content = "";

		if (randomFile == null) {
			//return content;
                              return "can not open log file";
		}

		try {
			// read the file from the ending position of last time
			randomFile.seek(oldFileSize);

			if (oldFileSize == randomFile.length()) {
				return "";
			}

			String tmp = "";
			while ((tmp = randomFile.readLine()) != null) {
				content += tmp;

			}
			oldFileSize = randomFile.length();

			return new String(content.getBytes("ISO8859-1"));

		} catch (IOException e) {
			e.printStackTrace();
			return "read log error";
		}

	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public long getOldFileSize() {
		return oldFileSize;
	}

	public void setOldFileSize(long oldFileSize) {
		this.oldFileSize = oldFileSize;
	}

}
