package com.bhle.access.batch.servlet;

import org.springframework.batch.admin.web.resources.ResourceService;

public class DefaultResouceService implements ResourceService {

	private String servletPath = "/batch-admin";

	/**
	 * @param servletPath
	 *            the servletPath to set
	 */
	public void setServletPath(String servletPath) {
		this.servletPath = servletPath;
	}

	public String getServletPath() {
		return servletPath;
	}

}
