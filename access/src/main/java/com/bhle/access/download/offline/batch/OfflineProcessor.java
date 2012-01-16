package com.bhle.access.download.offline.batch;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.bhle.access.download.JpegPackageGenerator;
import com.bhle.access.download.PdfGenerator;
import com.bhle.access.download.Resolution;
import com.bhle.access.download.offline.OfflineProduct;

@Component
public class OfflineProcessor implements ItemProcessor<String[], OfflineProduct> {

	private static final Logger logger = LoggerFactory
			.getLogger(OfflineProcessor.class);
	
	private StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
	
	public OfflineProduct process(String[] pageUris) throws Exception {
		JobParameters jobParameters = stepExecution.getJobParameters();

		String format = jobParameters.getString("format");
		String res = jobParameters.getString("resolution");
		Resolution resolution = new Resolution(res);
		
//		logger.info("Process item requested by: " + pageUris.getEmail());
		byte[] byteStream = null;
		String suffix = null;
		if (format.equals("pdf")){
			byteStream = PdfGenerator.generate(pageUris, resolution);
			suffix = "pdf";
		} else if (format.equals("jpg")){
			byteStream = JpegPackageGenerator.generate(pageUris, resolution);
			suffix = "zip";
		} else {
			throw new IllegalArgumentException("Unknown format: " + format);
		}
		
		String guid = jobParameters.getString("guid");
		String email = jobParameters.getString("email");
		long timestamp = jobParameters.getLong("timestamp");
		
		OfflineProduct product = new OfflineProduct();
		product.setByteStream(byteStream);
		product.setGuid(guid);
		product.setSuffix(suffix);
		product.setEmail(email);
		product.setTimestamp(new Date(timestamp));
		return product;
	}

}
