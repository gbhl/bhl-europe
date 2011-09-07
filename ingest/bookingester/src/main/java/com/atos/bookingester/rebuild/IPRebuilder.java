package com.atos.bookingester.rebuild;

import com.atos.bookingester.query.InformationPackage;

public class IPRebuilder {
	public static FedoraObject rebuild(InformationPackage pack){
		MetsObject mets = new MetsObject(pack);
		System.out.println("Parsing a: " + mets.getType() + " ID:" + mets.getID());
		RebuildStrategy strategy = RebuildStrategyFactory.getStrategy(mets.getType());
		return strategy.rebuildObject(mets);
	}
}
