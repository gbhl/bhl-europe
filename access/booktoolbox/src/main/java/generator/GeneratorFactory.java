package generator;

import java.util.List;

import util.Format;
import util.Resolution;

public class GeneratorFactory {
	public static Generator getGenerator(Format format, List<String> pidList,
			Resolution resolution) {
		switch (format) {
		case PDF:
			return new PdfGenerator(pidList, resolution);
		case JPEG:
			return new JpegPackageGenerator(pidList, resolution);
		case OCR:
			return new OcrGenerator(pidList);
		default:
			return null;
		}
	}
}
