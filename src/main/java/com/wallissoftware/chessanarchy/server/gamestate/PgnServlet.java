package com.wallissoftware.chessanarchy.server.gamestate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@Singleton
public class PgnServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String idStr = req.getParameter("id");
        final Long id;
        final boolean idSupplied = idStr != null;
        if (idSupplied) {
            id = Long.valueOf(idStr);
        } else {
            id = LatestGameStateId.get();
        }
        if (id != null) {
            final Objectify ofy = ObjectifyService.factory().begin();
            final GameState gameState = ofy.load().type(GameState.class).id(id).getValue();
            if (gameState != null) {
                resp.setContentType("text/x-chess-pgn");
                final String maxAge = idSupplied && gameState.isFinished() ? "31556926" : "0";
                resp.setHeader("cache-control", "public, max-age=" + maxAge);
                resp.getWriter().write(gameState.getPgn());
                return;
            }
        }
        resp.sendError(404);
    }

}
