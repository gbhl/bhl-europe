package com.bhle.access.convert;

import com.bhle.access.domain.DatastreamWrapper;
import com.bhle.access.domain.Derivative;


public interface DatastreamConvertor {
	public Derivative derive(DatastreamWrapper datastreamWrapper);

	public Derivative deriveInformation(DatastreamWrapper datastream);
	
	public String[] getContentModels();

	public String getDatastreamId();

	public String getDerivativeId();
	
	public String getDerivativeSuffix();
	
	public String getDerivativeMimeType();
}
