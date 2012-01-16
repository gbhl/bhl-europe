package com.bhle.access.aop;

import com.bhle.access.download.offline.Offlinable;

public class DownloadStub {
	
	@Offlinable
	public String downloadSomething(){
		return "Generated Something";
	}
}
