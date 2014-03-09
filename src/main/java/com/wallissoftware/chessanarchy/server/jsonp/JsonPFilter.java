package com.wallissoftware.chessanarchy.server.jsonp;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

@Singleton
public class JsonPFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			final String jsonP = req.getParameter("callback");
			if (jsonP != null) {
				final JsonPServeletResponseWrapper jsonPResp = new JsonPServeletResponseWrapper((HttpServletResponse) resp);
				jsonPResp.forceContentType("application/javascript");
				jsonPResp.getWriter().write(jsonP + "(");
				chain.doFilter(req, jsonPResp);
				jsonPResp.getWriter().write(");");
			} else {
				chain.doFilter(req, resp);
			}
		} else {
			chain.doFilter(req, resp);
		}

	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
