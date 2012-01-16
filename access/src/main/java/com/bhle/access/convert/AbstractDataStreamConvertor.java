package com.bhle.access.convert;

import java.io.InputStream;

import org.akubraproject.BlobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bhle.access.domain.DatastreamWrapper;
import com.bhle.access.domain.Derivative;

@Component
public abstract class AbstractDataStreamConvertor implements
		DatastreamConvertor {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractDataStreamConvertor.class);

	public boolean isApplicable(DatastreamWrapper datastream) {
		return datastream.getDigitalObject().hasModel(getContentModels())
				&& datastream.getDsid().equals(getDatastreamId());
	}

	public Derivative deriveInformation(DatastreamWrapper datastream) {
		if (isApplicable(datastream)) {
			Derivative derivative = new Derivative();
			derivative.addConvertor(this);
			derivative.setDsId(getDerivativeId());
			derivative.setPid(datastream.getDigitalObject().getPid());
			return derivative;
		} else {
			return null;
		}
	}

	public Derivative derive(DatastreamWrapper datastreamWrapper) {
		Derivative derivative = deriveInformation(datastreamWrapper);
		if (derivative != null) {
			derivative.setInputStream(doConvert(datastreamWrapper
					.getInputStream()));
			return derivative;
		} else {
			return null;
		}
	}

	public abstract InputStream doConvert(InputStream inputStream);

}
