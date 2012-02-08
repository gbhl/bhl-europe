package com.bhle.access.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.bhle.access.domain.DigitalObjectFactory;
import com.bhle.access.domain.DigitalObjectWrapper;
import com.bhle.access.util.FedoraUtil;

@Component
public class FedoraObjectReader implements ItemReader<DigitalObjectWrapper> {

	private static final Logger logger = LoggerFactory
			.getLogger(FedoraObjectReader.class);
	
	private int cursor;
	private List<String> pids;

	public DigitalObjectWrapper read() throws Exception,
			UnexpectedInputException, ParseException {
		if (pids == null) {
			pids = FedoraUtil.getAllObjectsPids();
		}

		if (cursor >= pids.size() - 1) {
			cursor = 0;
			pids = null;
			return null;
		} else {
			logger.info("Batch Read: " + pids.get(cursor));
			DigitalObjectWrapper result = DigitalObjectFactory.build(pids.get(cursor));
			cursor++;
			return result;
		}
	}

}
