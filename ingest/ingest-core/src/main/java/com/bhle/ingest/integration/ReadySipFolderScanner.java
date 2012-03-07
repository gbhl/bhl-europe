package com.bhle.ingest.integration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.DefaultDirectoryScanner;

public class ReadySipFolderScanner extends DefaultDirectoryScanner {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ReadySipFolderScanner.class);

	@Value("${ingest.directory.name}")
	private String SIP_DIR_NAME;

	@Value("${ingest.ready.filename}")
	private String INGEST_READY_FILENAME;
	
	@Value("${ingest.running.filename}")
	private String INGEST_RUNNING_FILENAME;
	
	@Value("${ingest.done.filename}")
	private String INGEST_DONE_FILENAME;

	@Override
	protected File[] listEligibleFiles(File directory) {
		
//		logger.info(directory.getAbsolutePath());
		
		if (directory.isFile() || !directory.canRead()) {
			return new File[0];
		}

		File[] rootFiles = directory.listFiles();
		List<File> files = new ArrayList<File>(rootFiles.length);
		for (File rootFile : rootFiles) {
			if (rootFile.isDirectory()
					&& rootFile.getName().equalsIgnoreCase(SIP_DIR_NAME)
					&& isReadySipFolder(rootFile)) {
				files.add(rootFile);
			} else {
				files.addAll(Arrays.asList(listEligibleFiles(rootFile)));
			}
		}

		return files.toArray(new File[files.size()]);
	}

	private boolean isReadySipFolder(File directory) {

		boolean isReady = false;

		boolean containsIngestReadyFile = false;
		boolean containsIngestProcessingFile = false;
		boolean containsIngestDoneFile = false;

		File[] rootFiles = directory.listFiles();

		for (File file : rootFiles) {
			if (file.getName().equalsIgnoreCase(INGEST_READY_FILENAME)) {
				containsIngestReadyFile = true;
			}
			if (file.getName().equalsIgnoreCase(INGEST_RUNNING_FILENAME)) {
				containsIngestProcessingFile = true;
			}
		}

		isReady = containsIngestReadyFile && !containsIngestProcessingFile
				&& !containsIngestDoneFile;

		if (isReady) {
			try {
				File processingFile = new File(directory, INGEST_RUNNING_FILENAME);
				processingFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return isReady;
	}
}
