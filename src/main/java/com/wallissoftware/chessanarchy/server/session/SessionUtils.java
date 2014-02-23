package com.wallissoftware.chessanarchy.server.session;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.wallissoftware.chessanarchy.server.words.Adjectives;
import com.wallissoftware.chessanarchy.server.words.Nouns;

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
}
