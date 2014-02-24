package com.wallissoftware.chessanarchy.server.session;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.wallissoftware.chessanarchy.server.words.Adjectives;
import com.wallissoftware.chessanarchy.server.words.Nouns;
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
			setColor(session, (Math.random() * 2) % 2 == 0 ? Color.WHITE : Color.BLACK);
		}
		if (session.getAttribute("black") != null) {

			if (System.currentTimeMillis() - (Long) session.getAttribute("black") > 20000) {
				return Color.BLACK;
			}
		}
		if (session.getAttribute("white") != null) {
			if (System.currentTimeMillis() - (Long) session.getAttribute("white") > 20000) {
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
}
