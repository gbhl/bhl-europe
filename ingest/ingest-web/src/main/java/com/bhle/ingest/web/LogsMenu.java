package com.bhle.ingest.web;

import org.springframework.batch.admin.web.resources.BaseMenu;
import org.springframework.stereotype.Component;


@Component
public class LogsMenu extends BaseMenu{
	
	public LogsMenu() {
		super("/logs", "Logs", 5);
	}

}
