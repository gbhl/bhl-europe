package com.bhle.access.domain;

import com.bhle.access.jms.util.FedoraAtomMessage;
import com.bhle.access.util.FedoraUtil;
import com.yourmediashelf.fedora.generated.access.DatastreamType;

public class DatastreamFactory {

	public static DatastreamWrapper build(FedoraAtomMessage atomMessage) {
		DatastreamWrapper datastreamWrapper = buildInformation(atomMessage);

		String pid = atomMessage.getPid();
		String dsid = atomMessage.getDsID();
		datastreamWrapper.setMimeType(FedoraUtil.getDatastreamMIME(pid, dsid));
		datastreamWrapper.setInputStream(FedoraUtil.getDatastreamDissemination(
				pid, dsid));
		return datastreamWrapper;
	}

	public static DatastreamWrapper buid(DatastreamType datastreamType,
			DigitalObjectWrapper objectWrapper) {
		DatastreamWrapper datastreamWrapper = new DatastreamWrapper(
				datastreamType.getDsid());
		
		String pid = objectWrapper.getPid();
		String dsid = datastreamType.getDsid();
		datastreamWrapper.setDigitalObject(objectWrapper);
		datastreamWrapper.setMimeType(FedoraUtil.getDatastreamMIME(pid, dsid));
		datastreamWrapper.setInputStream(FedoraUtil.getDatastreamDissemination(
				pid, dsid));
		return datastreamWrapper;
	}

	public static DatastreamWrapper buildInformation(
			FedoraAtomMessage atomMessage) {
		DatastreamWrapper datastreamWrapper = new DatastreamWrapper(
				atomMessage.getDsID());
		DigitalObjectWrapper objectWrapper = DigitalObjectFactory
				.build(atomMessage);
		datastreamWrapper.setDigitalObject(objectWrapper);
		return datastreamWrapper;
	}

}
