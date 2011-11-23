package consumer;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import util.EmailSender;
import util.FilePathCoder;
import util.GlobalParameter;
import util.Product;

public class OfflineConsumer implements ConsumerListener {
	String filePath = GlobalParameter.PATH_OFFLINE;
	private ConsumerThreadPool threadTool = new ConsumerThreadPool();

	@Override
	public void publish(final Product product) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					String encodedPath = outputToOfflineDirectory(product);
					sendNotificationEmail(product, encodedPath);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		threadTool.execute(t);
	}

	private void sendNotificationEmail(Product product, String encodedPath) throws MessagingException {
		EmailSender mailSender = new EmailSender(encodedPath);
		mailSender.send(product);
	}

	private String outputToOfflineDirectory(Product product) throws Exception {
		File path = new File(GlobalParameter.PATH_OFFLINE);
		if (!path.isDirectory()) {
			throw new Exception();
		}

		File folder = new File(path + File.separator
				+ FilePathCoder.getFolder(product));
		if (!folder.exists()) {
			folder.mkdir();
		}

		// path/email/fileName.formatSuffix
		String absolutePath = folder + File.separator
				+ URLEncoder.encode(product.getName(), "UTF-8") + "."
				+ product.getFormat().getFormatAsSuffix();

		File file = new File(absolutePath);

		FileOutputStream out = new FileOutputStream(file);
		product.getOutputStream().write(out);

		return FilePathCoder.encode(absolutePath);
	}
}
