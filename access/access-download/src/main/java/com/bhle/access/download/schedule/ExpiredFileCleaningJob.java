package com.bhle.access.download.schedule;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

public class ExpiredFileCleaningJob {

	@Value("${download.blobStore.path}")
	private String blobStoreBasePath;

	@Value("#{${download.blobStore.expiration.days} * 60 * 60 * 1000} ")
	private long expiration;

	@SuppressWarnings("unchecked")
	@Scheduled(cron = "0 0 0 * * ?")
	public synchronized void clean() {
		long now = System.currentTimeMillis();
		for (Iterator<File> iterator = FileUtils.iterateFiles(new File(
				blobStoreBasePath), null, true); iterator.hasNext();) {
			File file = iterator.next();
			if (file.isFile() && isExpired(file, now)) {
				file.delete();
			} else if (file.isDirectory()) {
				if (file.list().length == 0) {
					file.delete();
				}
			}
		}
	}

	public boolean isExpired(File file, long now) {
		return now - file.lastModified() > expiration;
	}
}
