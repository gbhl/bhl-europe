package generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import consumer.ConsumerListener;

import util.FedoraObjectService;
import util.FileNameExtractor;
import util.Format;
import util.GlobalParameter;
import util.Product;
import util.Resolution;

public class JpegPackageGenerator extends FedoraObjectService implements
		Generator {
	private List<ConsumerListener> listeners = new ArrayList<ConsumerListener>();

	private List<String> pids;
	private Resolution resolution;

	private Product product;

	public JpegPackageGenerator(List<String> pids, Resolution resolution) {
		this(pids, resolution, null);
	}

	public JpegPackageGenerator(List<String> pids, Resolution resolution,
			OutputStream outputStream) {
		this.pids = pids;
		this.resolution = resolution;
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
				writeZipToOutput(out);
			}
		};
		product.setOutputStream(outputStream);
		product.setFormat(Format.JPEG);
		return product;
	}

	private void writeZipToOutput(OutputStream out) {
		try {
			ZipOutputStream zipOut = new ZipOutputStream(out);
			for (String pid : pids) {
				AddPageEntry(pid, resolution, zipOut);
			}
			zipOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void AddPageEntry(String pid, Resolution resolution,
			ZipOutputStream out) {
		URL url;
		try {
			url = new URL(getJPEGURLFromPID(pid, resolution.getLevel()));
			InputStream in = url.openStream();

			ZipEntry entry = new ZipEntry(pid + ".jpg");
			out.putNextEntry(entry);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			out.closeEntry();
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
		this.listeners.add(listener);
	}
}
