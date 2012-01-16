package com.bhle.access.domain;

import com.bhle.access.jms.util.FedoraAtomMessage;
import com.bhle.access.util.FedoraUtil;
import com.yourmediashelf.fedora.generated.access.DatastreamType;

public class DigitalObjectFactory {

	public static DigitalObjectWrapper build(FedoraAtomMessage atomMessage) {
		String pid = atomMessage.getPid();
		DigitalObjectWrapper objectWrapper = new DigitalObjectWrapper(pid);
		setContentModels(objectWrapper);
		setDatastream(objectWrapper);
		return objectWrapper;
	}

	public static DigitalObjectWrapper build(String pid) {
		DigitalObjectWrapper objectWrapper = new DigitalObjectWrapper(pid);
		setContentModels(objectWrapper);
		setDatastream(objectWrapper);
		return objectWrapper;
	}

	private static void setDatastream(DigitalObjectWrapper objectWrapper) {
		for (DatastreamType datastreamType : FedoraUtil
				.listDatastreams(objectWrapper.getPid())) {
			DatastreamWrapper datastreamWrapper = DatastreamFactory.buid(
					datastreamType, objectWrapper);
			objectWrapper.addDatastreams(datastreamWrapper);
		}
	}

	private static void setContentModels(DigitalObjectWrapper objectWrapper) {
		objectWrapper.setConternModels(FedoraUtil
				.getContentModels(objectWrapper.getPid()));
	}

}
