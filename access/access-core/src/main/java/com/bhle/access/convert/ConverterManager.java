package com.bhle.access.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bhle.access.domain.DatastreamWrapper;
import com.bhle.access.domain.Derivative;
import com.bhle.access.domain.DigitalObjectWrapper;

public class ConverterManager {

	private static final Logger logger = LoggerFactory
			.getLogger(ConverterManager.class);

	public static List<DatastreamConverter> converters;

	@Autowired
	public void setConverters(List<DatastreamConverter> convertors) {
		ConverterManager.converters = convertors;
	}

	public static Derivative[] derive(DatastreamWrapper datastream) {
		List<Derivative> results = new ArrayList<Derivative>();
		for (DatastreamConverter convertor : converters) {
			Derivative derivative = convertor.derive(datastream);
			if (derivative != null) {
				logger.info("Convert " + datastream.getDigitalObject().getPid()
						+ "/" + convertor.getDatastreamId() + " to "
						+ convertor.getDerivativeId());
				results.add(derivative);
			}
		}
		return results.toArray(new Derivative[] {});
	}

	public static Derivative[] derive(DigitalObjectWrapper object) {
		List<Derivative> results = new ArrayList<Derivative>();
		for (DatastreamWrapper datastreamWrapper : object.getDatastreams()) {
			Derivative[] derivatives = derive(datastreamWrapper);
			if (derivatives.length == 0) {
				datastreamWrapper.close();
			} else {
				results.addAll(Arrays.asList(derivatives));
			}
		}
		return results.toArray(new Derivative[] {});
	}

	public static Derivative[] deriveInformation(DatastreamWrapper datastream) {
		List<Derivative> results = new ArrayList<Derivative>();
		for (DatastreamConverter convertor : converters) {
			Derivative derivative = convertor.deriveInformation(datastream);
			if (derivative != null) {
				results.add(derivative);
			}
		}
		return results.toArray(new Derivative[] {});
	}

	public static String getMimeType(String dsid) {
		for (DatastreamConverter convertor : converters) {
			if (convertor.getDerivativeId().equalsIgnoreCase(dsid)) {
				return convertor.getDerivativeMimeType();
			}
		}
		return null;
	}

	public static String getSuffix(String dsid) {
		for (DatastreamConverter convertor : converters) {
			if (convertor.getDerivativeId().equalsIgnoreCase(dsid)) {
				return convertor.getDerivativeSuffix();
			}
		}
		return null;
	}

}
