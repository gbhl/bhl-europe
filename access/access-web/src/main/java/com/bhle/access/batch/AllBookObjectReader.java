package com.bhle.access.batch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.bhle.access.util.FedoraUtil;

public class AllBookObjectReader implements ItemReader<String> {

	private String[] bookObjects;

	private int index = 0;

	@BeforeStep
	public void init(StepExecution stepExecution) {
		this.bookObjects = FedoraUtil.getAllBookObjects();
		this.index = 0;
	}

	@Override
	public String read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		if (index <= bookObjects.length - 1) {
			String pid = bookObjects[index].split("/")[1];
			index++;
			return pid;
		}
		return null;
	}
}
