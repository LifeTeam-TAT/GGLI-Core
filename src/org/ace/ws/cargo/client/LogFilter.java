package org.ace.ws.cargo.client;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class LogFilter implements Filter {
	private static final Logger logger = Logger.getLogger(LogFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		// use wrapper to read multiple times the content
		ServletRequestWrapper request = new ServletRequestWrapper((HttpServletRequest) req);
		if (request.getMethod().equalsIgnoreCase("POST")) {
			logger.debug("========== Request Body ==========");
			logger.debug(IOUtils.toString(request.getInputStream()));
		}
		chain.doFilter(request, resp);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
