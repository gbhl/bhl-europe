package com.atos.bookingester;

import com.atos.bookingester.ingest.Ingester;
import com.atos.bookingester.query.InformationPackage;
import com.atos.bookingester.query.IngestQueue;
import com.atos.bookingester.query.LocalIngestQueue;
import com.atos.bookingester.query.RemoteIngestQueue;
import com.atos.bookingester.rebuild.IPRebuilder;
import com.atos.bookingester.rebuild.FedoraObject;

public class App {
	private static boolean IS_TEST_MODE = false;

	public static void main(String[] args) {
		if (args.length != 3 && args.length != 5) {
			System.err
					.println("Please enter the base URL, username, and password as parameters.");
			return;
		}

		if (args.length == 5 && args[3].equals("testmode")) {
			IS_TEST_MODE = true;
		}

		Ingester ingester = new Ingester(args[0], args[1], args[2]);

		IngestQueue queue;
		if (IS_TEST_MODE) {
			queue = new LocalIngestQueue(args[4]);
		} else {
			queue = new RemoteIngestQueue();
		}

		do {
			queue.query();

			if (!queue.isEmpty()) {
				// IngestQueue.reset();

				for (InformationPackage pack : queue.getInformationPackages()) {
					FedoraObject object = IPRebuilder.rebuild(pack);

					// Write log
					ingester.ingest(object);
					// Write log
				}
			} else {
				System.out.println("It's empty, Start again!");
			}
			if (!IS_TEST_MODE) {
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} while (!IS_TEST_MODE);
	}
}
