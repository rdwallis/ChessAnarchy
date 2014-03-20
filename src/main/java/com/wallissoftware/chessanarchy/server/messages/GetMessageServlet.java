package com.wallissoftware.chessanarchy.server.messages;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.wallissoftware.chessanarchy.shared.CAConstants;

@Singleton
public class GetMessageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_QUEUE_KEY = "mq";

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String idStr = req.getParameter("id");

        final Long id;
        final boolean idSupplied = idStr != null;
        if (idSupplied) {
            id = Long.valueOf(idStr);

        } else {
            id = LatestMessageId.get();
        }
        if (id != null) {

            final Objectify ofy = ObjectifyService.ofy();
            final MessageCache messageCache = ofy.load().type(MessageCache.class).id(id).getValue();
            if (messageCache != null) {
                resp.setContentType("application/json");
                final String maxAge = idSupplied ? "31556926" : ((int) (CAConstants.SYNC_DELAY / 1000)) + "";
                resp.setHeader("cache-control", "public, max-age=" + maxAge);

                resp.getWriter().write(messageCache.getJson());

                return;
            }
        }
        resp.sendError(404);

    }

    @Override
    protected long getLastModified(final HttpServletRequest req) {
        if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
            return super.getLastModified(req);
        }
        final Long lastUpdateTime = LastUpdateTime.getLastUpdateTime();
        if (lastUpdateTime == null) {
            return System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - lastUpdateTime > 30000) {
            final Queue queue = QueueFactory.getDefaultQueue();
            queue.add(withUrl("/admin/processmessages").method(Method.GET));
        }
        return lastUpdateTime;
    }

}
