package com.atos.bookingester.rebuild;

import com.atos.bookingester.query.InformationPackage;

public interface RebuildStrategy {
	FedoraObject rebuildObject(MetsObject mets);
}
