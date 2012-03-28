package com.bhle.access.oai;

import java.util.Collection;

import proai.MetadataFormat;

public interface MetadataFormatProvider {
	public Collection<MetadataFormat> getMetadataFormatCollection();
}
