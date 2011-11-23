package generator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import consumer.ConsumerListener;

import util.FedoraObjectService;
import util.FileNameExtractor;
import util.Format;
import util.Product;
import util.Resolution;

public class PdfGenerator extends FedoraObjectService implements Generator {
	private List<ConsumerListener> listeners = new ArrayList<ConsumerListener>();

	private List<String> pids;
	private Resolution resolution;

	private Product product;

	public PdfGenerator(List<String> pids, Resolution resolution) {
		this(pids, resolution, null);
	}

	public PdfGenerator(List<String> pids, Resolution resolution,
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
				writePDFToOutput(out);
			}
		};
		product.setOutputStream(outputStream);
		product.setFormat(Format.PDF);

		for (ConsumerListener listener : listeners) {
			listener.publish(product);
		}

		return product;
	}

	private void writePDFToOutput(OutputStream out) {
		Document document = initialPDF();

		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			mergeAllPages(pids, resolution, writer, document);
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

	private Document initialPDF() {
		Document document = new Document();
		document.setMargins(0, 0, 0, 0);
		return document;
	}

	private void mergeAllPages(List<String> pidList, Resolution resolution,
			PdfWriter writer, Document document) {
		for (String pid : pidList) {
			try {
				Image image = Image.getInstance(new URL(getJPEGURLFromPID(pid,
						resolution.getLevel())));
				configureImage(image, document);
				document.add(image);
				if (pidList.indexOf(pid) != pidList.size() - 1) {
					document.newPage();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (BadElementException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
	}

	private void configureImage(Image image, Document document) {
		image.setAlignment(Element.ALIGN_CENTER);
		image.scaleToFit(document.getPageSize().getWidth(), document
				.getPageSize().getHeight());
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
