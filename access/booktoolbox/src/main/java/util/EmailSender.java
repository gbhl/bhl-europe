package util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
	private String retrievingPath = null;
	private Session mailSession = null;

	public EmailSender(String retrivingPath) {
		this.retrievingPath = retrivingPath;

		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", GlobalParameter.MAIL_HOST);
		props.setProperty("mail.user", GlobalParameter.MAIL_USER);
		props.setProperty("mail.password", GlobalParameter.MAIL_PASSWORD);

		this.mailSession = Session.getDefaultInstance(props, null);
	}

	public void send(Product product) throws MessagingException {

		Transport transport = mailSession.getTransport();

		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject("Testing javamail plain");
		message.setContent(new OfflineEmailHtml(product.getLang())
				.getNotification(retrievingPath), "text/html");
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				product.getEmail()));

		transport.connect();
		transport.sendMessage(message,
				message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}
}
