package com.atos.bookingester.query;

import java.util.List;

public interface IngestQueue {
	public void query();
	public boolean isEmpty();
	public List<InformationPackage> getInformationPackages();
	public void reset();
}
