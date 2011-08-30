package com.atos.bookingester.rebuild;

import com.atos.bookingester.query.InformationPackage;

public class IPRebuilder {
	public static FedoraObject rebuild(InformationPackage pack){
		MetsObject mets = new MetsObject(pack);
		FedoraObjectType type = mets.getType();
		System.out.println("Parsing a: " + type);
		RebuildStrategy strategy = RebuildStrategyFactory.getStrategy(type);
		return strategy.rebuildObject(mets);
	}
}
