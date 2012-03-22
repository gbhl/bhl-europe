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

public class ConvertorManager {

	private static final Logger logger = LoggerFactory
			.getLogger(ConvertorManager.class);

	public static List<DatastreamConvertor> convertors;

	@Autowired
	public void setConvertors(List<DatastreamConvertor> convertors) {
		ConvertorManager.convertors = convertors;
	}

	public static Derivative[] derive(DatastreamWrapper datastream) {
		List<Derivative> results = new ArrayList<Derivative>();
		for (DatastreamConvertor convertor : convertors) {
			Derivative derivative = convertor.derive(datastream);
			if (derivative != null) {
				logger.info("Convert " + datastream.getDigitalObject().getPid() + "/" + convertor.getDatastreamId() + " to "
						+ convertor.getDerivativeId());
				results.add(derivative);
			}
		}
		datastream.close();
		return results.toArray(new Derivative[] {});
	}

	public static Derivative[] derive(DigitalObjectWrapper object) {
		List<Derivative> results = new ArrayList<Derivative>();
		for (DatastreamWrapper datastreamWrapper : object.getDatastreams()) {
			results.addAll(Arrays.asList(derive(datastreamWrapper)));

		}
		return results.toArray(new Derivative[] {});
	}

	public static Derivative[] deriveInformation(DatastreamWrapper datastream) {
		List<Derivative> results = new ArrayList<Derivative>();
		for (DatastreamConvertor convertor : convertors) {
			Derivative derivative = convertor.deriveInformation(datastream);
			if (derivative != null) {
				results.add(derivative);
			}
		}
		return results.toArray(new Derivative[] {});
	}

	public static String getMimeType(String dsid) {
		for (DatastreamConvertor convertor : convertors) {
			if (convertor.getDerivativeId().equalsIgnoreCase(dsid)) {
				return convertor.getDerivativeMimeType();
			}
		}
		return null;
	}
	
	public static String getSuffix(String dsid) {
		for (DatastreamConvertor convertor : convertors) {
			if (convertor.getDerivativeId().equalsIgnoreCase(dsid)) {
				return convertor.getDerivativeSuffix();
			}
		}
		return null;
	}

}
