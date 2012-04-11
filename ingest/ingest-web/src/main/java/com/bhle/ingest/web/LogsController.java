package com.bhle.ingest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * Controller for showing the logback information
 * @author li_zhaoyu
 *
 */

@Controller
public class LogsController {

	@Autowired
	private ContentProvider contentProvider;
	
	
	
	public void setContentProvider(ContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	@RequestMapping(value="/logs", method=RequestMethod.GET)
    public String showLog() {
		
		//When the first time open the "logs" page, initial the 
		//beginning position of file reading
		contentProvider.openFile();
		contentProvider.setOldFileSize(0);
		
		
        return "logs"; 
    }
	
	@ResponseBody
	@RequestMapping(value="/refresh", method=RequestMethod.GET)
    public String refreshLog() { 
//		if (!contentProvider.openFile()){
//			return "Cannot open the log of ingest";
//		}
		
	
		return contentProvider.getContent();
    }
	

}
