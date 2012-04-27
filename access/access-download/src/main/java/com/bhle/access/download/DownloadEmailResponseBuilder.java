package com.bhle.access.download;

import org.akubraproject.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class DownloadEmailResponseBuilder implements DownloadResponseBuilder {

	@Autowired
	private MailSender mailSender;

	@Autowired
	@Qualifier("successfulMail")
	private SimpleMailMessage successfulMail;

	@Override
	public DownloadResponse build(DownloadRequest request, Blob blob) {
		OfflineDownloadResponse response = new OfflineDownloadResponse();
		response.setRequest(request);
		response.setBlob(blob);

		if (request.isOffline()) {
			sendEmail(request, response);
		}

		return response;
	}

	private void sendEmail(DownloadRequest request,
			OfflineDownloadResponse response) {
		SimpleMailMessage message = new SimpleMailMessage(successfulMail);
		message.setText(successfulMail.getText().replace(
				"[link]",
				DownloadLocationHelper.encrypt(response.getBlob().getId()
						.getSchemeSpecificPart())));
		message.setTo(((OfflineDownloadRequest) request).getEmail());
		mailSender.send(message);
	}
}
