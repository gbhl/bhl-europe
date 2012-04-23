package com.bhle.access.bookreader.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.bhle.access.bookreader.BookInfo;
import com.bhle.access.bookreader.BookInfoBuilder;
import com.bhle.access.util.FedoraURI;

public class BookInfoTasklet implements Tasklet {

	private String pid;

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		String guid = FedoraURI.getGuidFromPid(pid);

		BookInfo info = BookInfoBuilder.build(guid);
		BookInfoBuilder.save(info);
		return RepeatStatus.FINISHED;
	}

}
