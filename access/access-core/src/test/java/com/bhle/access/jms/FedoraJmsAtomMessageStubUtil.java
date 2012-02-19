package com.bhle.access.jms;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

public class FedoraJmsAtomMessageStubUtil {

	private Resource ingestMessage;

	private Resource purgeObjectMessage;
	private Resource purgeDatastreamMessage;

	private Resource addDatastreamMessage;

	private Resource modifyObjectMessage;
	private Resource modifyDatastreamMessage;

	public void setIngestMessage(Resource ingestMessage) {
		this.ingestMessage = ingestMessage;
	}

	public void setPurgeObjectMessage(Resource purgeObjectMessage) {
		this.purgeObjectMessage = purgeObjectMessage;
	}

	public void setPurgeDatastreamMessage(Resource purgeDatastreamMessage) {
		this.purgeDatastreamMessage = purgeDatastreamMessage;
	}

	public void setAddDatastreamMessage(Resource addDatastreamMessage) {
		this.addDatastreamMessage = addDatastreamMessage;
	}

	public void setModifyObjectMessage(Resource modifyObjectMessage) {
		this.modifyObjectMessage = modifyObjectMessage;
	}

	public void setModifyDatastreamMessage(Resource modifyDatastreamMessage) {
		this.modifyDatastreamMessage = modifyDatastreamMessage;
	}

	public String getIngestPlainMessage() {
		return convertResourceToAtom(ingestMessage);
	}

	public String getPurgeObjectPlainMessage() {
		return convertResourceToAtom(purgeObjectMessage);
	}

	public String getPurgeDatastreamPlainMessage() {
		return convertResourceToAtom(purgeDatastreamMessage);
	}

	public String getAddDatastreamPlainMessage() {
		return convertResourceToAtom(addDatastreamMessage);
	}

	public String getModifyObjectPlainMessage() {
		return convertResourceToAtom(modifyObjectMessage);
	}

	public String getModifyDatastreamPlainMessage() {
		return convertResourceToAtom(modifyDatastreamMessage);
	}

	private String convertResourceToAtom(Resource resource) {
		try {
			return IOUtils.toString(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
