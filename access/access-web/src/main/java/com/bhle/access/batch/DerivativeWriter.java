package com.bhle.access.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.bhle.access.domain.Derivative;
import com.bhle.access.storage.StorageService;

@Component
public class DerivativeWriter implements ItemWriter<Derivative[]> {

	private StorageService store;
	
	public void setStore(StorageService store) {
		this.store = store;
	}

	public void write(List<? extends Derivative[]> items) throws Exception {
		for (Derivative[] derivatives : items) {
			for (Derivative derivative : derivatives) {
				store.updateDerivative(derivative);
				derivative.close();
			}
		}

	}

}
