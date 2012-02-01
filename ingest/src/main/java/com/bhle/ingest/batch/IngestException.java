package com.bhle.ingest.batch;

public class IngestException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public IngestException(Throwable t) {
		super(t);
	}
}
