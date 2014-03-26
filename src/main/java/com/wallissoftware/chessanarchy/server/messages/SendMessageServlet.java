package com.wallissoftware.chessanarchy.server.messages;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.wallissoftware.chessanarchy.server.gamestate.GameState;
import com.wallissoftware.chessanarchy.server.gamestate.LatestGameStateId;
import com.wallissoftware.chessanarchy.server.session.SessionUtils;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.message.Message;
import com.wallissoftware.chessanarchy.shared.message.MessageImpl;

@Singleton
public class SendMessageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final int SHARD_COUNT = 10;
    public static final String MESSAGE_QUEUE_KEY = "mq";

    public static Random random = new Random();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

        final String message = StringEscapeUtils.escapeHtml(req.getParameter("msg")).replace("\u003d", "=");

        if (message != null && !message.isEmpty()) {
            boolean sessionModified = false;

            if (message.toLowerCase().startsWith("/nick")) {
                String name = message.substring(5);
                name = name.replace(" ", "");
                if (name.length() > 20) {
                    name = name.substring(0, 20);
                }
                SessionUtils.setName(req.getSession(), name);
                sessionModified = true;
            }
            if (message.toLowerCase().startsWith("/team")) {
                final String clr = message.substring(5).trim().toUpperCase();
                try {
                    Color color = Color.valueOf(clr);
                    final Long id = LatestGameStateId.get();

                    if (id != null) {
                        final Objectify ofy = ObjectifyService.ofy();
                        final GameState gameState = ofy.load().type(GameState.class).id(id).getValue();
                        if (gameState != null) {
                            if (gameState.swapColors()) {
                                color = color == Color.WHITE ? Color.BLACK : Color.WHITE;
                            }
                            SessionUtils.setColor(req.getSession(), color);
                            sessionModified = true;
                        }
                    }

                } catch (final Exception e) {

                }
            }
            final Message msg = new MessageImpl(SessionUtils.getName(req.getSession()), SessionUtils.getUserId(req.getSession()), message, UUID.randomUUID().toString(), SessionUtils.getColor(req.getSession()), System.currentTimeMillis());

            final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

            final String messageQueueShardKey = MESSAGE_QUEUE_KEY + random.nextInt(SHARD_COUNT);
            @SuppressWarnings("unchecked")
            Set<Message> messageQueue = (Set<Message>) cache.get(messageQueueShardKey);
            if (messageQueue == null) {
                messageQueue = new HashSet<Message>();
            }
            messageQueue.add(msg);
            cache.put(messageQueueShardKey, messageQueue);
            if (LastUpdateTime.isTimeToUpdate()) {
                final Queue queue = QueueFactory.getDefaultQueue();
                queue.add(withUrl("/admin/processmessages").method(Method.GET));
            }
            resp.setContentType("application/json");
            final Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("message", msg);
            if (sessionModified) {
                resultMap.put("user", SessionUtils.getUserMap(req.getSession(), resp));
            }
            new Gson().toJson(resultMap, resp.getWriter());

        }

    }
}
