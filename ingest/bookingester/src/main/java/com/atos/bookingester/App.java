package com.atos.bookingester;


import com.atos.bookingester.ingest.Ingester;
import com.atos.bookingester.query.InformationPackage;
import com.atos.bookingester.query.IngestQueue;
import com.atos.bookingester.rebuild.IPRebuilder;
import com.atos.bookingester.rebuild.FedoraObject;

public class App {
	public static void main(String[] args) {
		if (args.length != 3){
			System.err.println("Please enter the base URL, username, and password as parameters.");
			return;
		}
		
		Ingester ingester = new Ingester(args[0], args[1], args[2]);
		
		while (true) {
			IngestQueue.query();

			if (!IngestQueue.isEmpty()) {
				// IngestQueue.reset();

				for (InformationPackage pack : IngestQueue
						.getInformationPackages()) {
					FedoraObject object = IPRebuilder.rebuild(pack);

					// Write log
					ingester.ingest(object);
					// Write log
				}
			} else {
				System.out.println("It's empty, Start again!");
			}
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
