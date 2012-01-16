package com.bhle.access.download.offline.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.bhle.access.download.offline.OfflineProduct;
import com.bhle.access.download.offline.storage.OfflineStorage;

@Component
public class OfflineProductWriter implements ItemWriter<OfflineProduct>{

	private static final Logger logger = LoggerFactory
			.getLogger(OfflineProductWriter.class);
	
	private OfflineStorage store;
	
	public void setStore(OfflineStorage store) {
		this.store = store;
	}
	
	public void write(List<? extends OfflineProduct> items) throws Exception {
		for(OfflineProduct product : items){
			logger.info("Write Product: " + product.getFileName());
			store.save(product);
		}
		
	}

}
