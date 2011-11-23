package util;

import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

public class GlobalParameter {
	private static ResourceBundle bundle = ResourceBundle
			.getBundle("booktoolbox");;

	public static String URL_FEDORA = bundle.getString("fedoraURL");
	public static String URL_RISEARCH = bundle.getString("risearchURL");

	public static String PID_PAGE_SDEF = bundle.getString("pid.pageSdef");
	public static String PID_BOOK_SDEF = bundle.getString("pid.bookSdef");
	public static String PID_BOOK_CMODEL = bundle.getString("pid.bookCModel");
	public static String PID_PAGE_CMODEL = bundle.getString("pid.pageCModel");

	public static String DS_ID_OCR = bundle.getString("dsid.OCR");
	public static String DS_ID_COORDINATE = bundle
			.getString("dsid.coordinates");
	public static String DS_ID_JP2 = bundle.getString("dsid.JP2");

	public static String METHOD_JPEG_LEVEL = bundle
			.getString("method.jpeg.level");
	public static String METHOD_PDF_RESOLUTION = bundle
			.getString("method.pdf.resolution");

	public static int LEVEL_HIGH_RESOLUTION = Integer.valueOf(bundle
			.getString("resolution.to.djatoka.level.high"));
	public static int LEVEL_MEDIUM_RESOLUTION = Integer.valueOf(bundle
			.getString("resolution.to.djatoka.level.medium"));
	public static int LEVEL_LOW_RESOLUTION = Integer.valueOf(bundle
			.getString("resolution.to.djatoka.level.low"));

	public static String MAIL_HOST = bundle.getString("mail.host");
	public static String MAIL_USER = bundle.getString("mail.user");
	public static String MAIL_PASSWORD = bundle.getString("mail.password");

	public static String PATH_EMAIL = bundle.getString("path.email");
	public static String PATH_RESPONSE = bundle.getString("path.response");
	public static String PATH_NOTIFICATION = bundle.getString("path.notification");
	public static String PATH_OFFLINE = bundle
			.getString("path.offline.dir.path");

	public static int MAX_THREAD = Integer.valueOf(bundle
			.getString("max.thread"));

	private static Semaphore sem = new Semaphore(MAX_THREAD);

	public static void acquire() {
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void release() {
		sem.release();
	}

	public static boolean moreSemaphore() {
		return sem.availablePermits() <= 0;
	}
}
