package com.bhle.access.bookreader.batch;

import java.io.InputStream;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.bhle.access.bookreader.ThumbnailBuilder;
import com.bhle.access.util.FedoraURI;

public class ThumbnailTasklet implements Tasklet {

	private String pid;

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		String guid = FedoraURI.getGuidFromPid(pid);

		InputStream in = ThumbnailBuilder.build(guid);
		ThumbnailBuilder.save(guid, in);
		return RepeatStatus.FINISHED;
	}

}
