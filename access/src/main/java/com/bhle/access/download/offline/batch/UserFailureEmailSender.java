package com.bhle.access.download.offline.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class UserFailureEmailSender implements ItemWriter<FailureReport> {
	private static final Logger logger = LoggerFactory
			.getLogger(UserFailureEmailSender.class);
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private SimpleMailMessage mail;
	
	public void write(List<? extends FailureReport> reports) throws Exception {
		for (FailureReport report: reports){
			logger.info("Send email to: " + report.getEmail());
			mail.setText(String.format(mail.getText(), report.getGuid(), report.getTimestamp()));
			mail.setTo(report.getEmail());
			mailSender.send(mail);
		}
		
	}
	
}
