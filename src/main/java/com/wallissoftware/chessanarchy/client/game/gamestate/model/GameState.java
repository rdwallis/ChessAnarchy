package com.wallissoftware.chessanarchy.client.game.gamestate.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.wallissoftware.chessanarchy.shared.government.Government;

public final class GameState extends JavaScriptObject {

	protected GameState() {
	};

	public native String getId()/*-{
		return this.id;
	}-*/;

	public native boolean swapColors() /*-{
		return this.swapColors;
	}-*/;

	private native JsArrayString getNativeMoveList() /*-{
		return this.moveList;
	}-*/;

	public List<String> getMoveList() {
		final List<String> result = new ArrayList<String>();
		for (int i = 0; i < getNativeMoveList().length(); i++) {
			result.add(getNativeMoveList().get(i));
		}
		return result;
	}

	private native String getNativeWhiteGovernment()/*-{
		return this.whiteGovernment;
	}-*/;

	private native String getNativeBlackGovernment()/*-{
		return this.blackGovernment;
	}-*/;

	public Government getWhiteGovernment() {
		return Government.valueOf(getNativeWhiteGovernment());
	}

	public Government getBlackGovernment() {
		return Government.valueOf(getNativeBlackGovernment());
	}

	public static GameState fromJson(final String json) {
		return JsonUtils.safeEval(json);
	}

}
