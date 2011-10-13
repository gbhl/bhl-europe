package com.atos.bookingester.rebuild;

public interface RebuildStrategy {
	FedoraObject rebuildObject(MetsObject mets);
}
