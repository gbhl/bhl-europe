package com.bhle.ingest.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.bhle.ingest.FedoraServiceImpl;

public class BookActivator implements Tasklet{
	
	private static final Logger logger = LoggerFactory
			.getLogger(BookActivator.class);
	
	private FedoraServiceImpl fedoraService;
	
	private String guid;
	
	public void setFedoraService(FedoraServiceImpl fedoraService) {
		this.fedoraService = fedoraService;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	
	 public RepeatStatus execute(StepContribution contribution, ChunkContext context)
             throws Exception {
		 
		 if (!guid.equals("")){
			 activateItem(guid);
		 }
       
        return RepeatStatus.FINISHED;
   }
	 
	 private void activateItem(String guid) {
			String pid = guid.replace("/", "-");
			logger.info("Activate object: {}", pid);
			fedoraService.activate(pid);
		}

}
