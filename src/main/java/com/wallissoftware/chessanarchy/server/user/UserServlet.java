package com.wallissoftware.chessanarchy.server.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.wallissoftware.chessanarchy.server.session.SessionUtils;

@Singleton
public class UserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/javascript");
		resp.getWriter().write("var chessAnarchy = {\"user\":" + new Gson().toJson(SessionUtils.getUserMap(req.getSession(), resp)) + "};");

	}

}
