package com.bhle.access.download.offline.storage;

import java.io.IOException;

import com.bhle.access.download.offline.OfflineProduct;

public interface OfflineStorage {
	public void save(OfflineProduct product) throws IOException;
}
