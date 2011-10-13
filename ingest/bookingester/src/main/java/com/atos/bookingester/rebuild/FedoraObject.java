package com.atos.bookingester.rebuild;

import java.util.List;
import java.util.Map;

public interface FedoraObject {

	public DublinCore getDC();

	public List<ExternalRelationship> getRelExts();

	public Map<String, DataStream> getDatastreams();

	public List<FedoraObject> getChildren();

}
