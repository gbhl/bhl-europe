package com.bhle.access.oai;

import java.util.Collection;
import java.util.Date;

import proai.Record;

public interface RecordProvider {
	Collection<Record> getRecordCollection(Date from, Date until,
			String mdPrefix);
}
