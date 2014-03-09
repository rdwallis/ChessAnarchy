package com.wallissoftware.chessanarchy.server.jsonp;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class JsonPServeletResponseWrapper extends HttpServletResponseWrapper {

	public JsonPServeletResponseWrapper(final HttpServletResponse response) {
		super(response);
	}

	public void forceContentType(final String type) {
		super.setContentType(type);
	}

	@Override
	public void setContentType(final String type) {

	}

	@Override
	public void setHeader(final String name, final String value) {

		if (!name.equals("Content-Type")) {
			super.setHeader(name, value);
		}
	}

	@Override
	public void addHeader(final String name, final String value) {
		if (!name.equals("Content-Type")) {
			super.addHeader(name, value);
		}

	}

	@Override
	public String getContentType() {
		return super.getContentType();
	}

}
