package com.wallissoftware.chessanarchy.server.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wallissoftware.chessanarchy.server.words.Adjectives;
import com.wallissoftware.chessanarchy.server.words.Nouns;
import com.wallissoftware.chessanarchy.shared.CAConstants;
import com.wallissoftware.chessanarchy.shared.game.Color;

public class SessionUtils {

	public static String getUserId(final HttpSession session) {
		if (session.getAttribute("userId") == null) {
			session.setAttribute("userId", UUID.randomUUID().toString());

		}
		return (String) session.getAttribute("userId");
	}

	public static String getName(final HttpSession session) {
		if (session.getAttribute("name") == null) {
			session.setAttribute("name", generateName());
		}
		return (String) session.getAttribute("name");
	}

	private static String generateName() {
		return capitalizeFirstLetter(Adjectives.getRandom()) + capitalizeFirstLetter(Nouns.getRandom());
	}

	private static String capitalizeFirstLetter(final String original) {
		if (original.length() == 0)
			return original;
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public static void setName(final HttpSession session, final String name) {
		session.setAttribute("name", name);

	}

	public static Color getColor(final HttpSession session) {
		if (session.getAttribute("white") == null && session.getAttribute("black") == null) {
			setColor(session, (Math.random() < 0.5) ? Color.WHITE : Color.BLACK);
		}
		if (session.getAttribute("black") != null) {

			if (System.currentTimeMillis() - (Long) session.getAttribute("black") > CAConstants.JOIN_TEAM_WAIT) {
				return Color.BLACK;
			}
		}
		if (session.getAttribute("white") != null) {
			if (System.currentTimeMillis() - (Long) session.getAttribute("white") > CAConstants.JOIN_TEAM_WAIT) {
				return Color.WHITE;
			}
		}
		return null;
	}

	public static void setColor(final HttpSession session, final Color color) {
		if (color == Color.WHITE) {
			session.removeAttribute("black");
			session.setAttribute("white", System.currentTimeMillis());
		} else {
			session.removeAttribute("white");
			session.setAttribute("black", System.currentTimeMillis());
		}

	}

	public static Map<String, String> getUserMap(final HttpSession session, final HttpServletResponse resp) {

		final Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap.put("name", getName(session));
		jsonMap.put("userId", getUserId(session));
		jsonMap.put("sessionId", session.getId());

		getColor(session);
		if (session.getAttribute("black") != null) {
			jsonMap.put("black", session.getAttribute("black") + "");
		}
		if (session.getAttribute("white") != null) {
			jsonMap.put("white", session.getAttribute("white") + "");
		}
		return jsonMap;
	}
}
