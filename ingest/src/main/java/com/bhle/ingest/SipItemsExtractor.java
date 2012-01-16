package com.bhle.ingest;

import java.io.File;
import java.util.List;

interface SipItemsExtractor {
	public List<File> getItems(Sip sip);
}
