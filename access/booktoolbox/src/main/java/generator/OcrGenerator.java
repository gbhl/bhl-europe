package generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import consumer.ConsumerListener;

import util.FedoraObjectService;
import util.FileNameExtractor;
import util.Format;
import util.Product;
import util.Resolution;

public class OcrGenerator extends FedoraObjectService implements Generator {
	private List<String> pids;

	private Product product;

	public OcrGenerator(List<String> pids) {
		this.pids = pids;
	}

	@Override
	public Product generate() {
		if (product == null) {
			product = Product.newRealtimeProduct(
					FileNameExtractor.abstractName(pids), null);
		}

		StreamingOutput outputStream = new StreamingOutput() {
			@Override
			public void write(OutputStream out) throws IOException,
					WebApplicationException {
				writeOcrToOutput(out);
			}
		};
		product.setOutputStream(outputStream);
		product.setFormat(Format.OCR);
		return product;
	}

	private void writeOcrToOutput(OutputStream out) {
		PrintWriter writer = new PrintWriter(out);
		for (String pid : pids) {
			writePageOCR(pid, writer);
		}
		writer.close();
	}

	private void writePageOCR(String pid, PrintWriter writer) {

		try {
			URL url = new URL(getOCRURLFromPID(pid));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String str;
			while ((str = in.readLine()) != null) {
				writer.println(str);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Product call() throws Exception {
		return generate();
	}

	@Override
	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public void addConsumerListener(ConsumerListener listener) {
		return;
	}
}
