package com.wallissoftware.chessanarchy.server.mainpage;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;
import com.wallissoftware.chessanarchy.server.session.SessionUtils;

@Singleton
public class MainPage extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/html");
		req.setAttribute("userJson", SessionUtils.getUserJson(req.getSession()));
		req.getRequestDispatcher("/ChessAnarchy.jsp").include(req, resp);
	}

}
