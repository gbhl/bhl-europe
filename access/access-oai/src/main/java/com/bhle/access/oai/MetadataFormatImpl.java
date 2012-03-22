package com.bhle.access.oai;

import proai.MetadataFormat;

public class MetadataFormatImpl implements MetadataFormat {

	private String namespaceURI;
	private String datastream;
	private String prefix;
	private String schemaLocation;

	public MetadataFormatImpl(String prefix, String datastream,
			String namespaceURI, String schemaLocation) {
		super();
		this.namespaceURI = namespaceURI;
		this.datastream = datastream;
		this.prefix = prefix;
		this.schemaLocation = schemaLocation;
	}

	@Override
	public String getNamespaceURI() {
		return namespaceURI;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getSchemaLocation() {
		return schemaLocation;
	}

	public String getDatastream() {
		return datastream;
	}

	@Override
	public String toString() {
		return prefix + " " + datastream + " " + namespaceURI + " "
				+ schemaLocation;
	}

}
