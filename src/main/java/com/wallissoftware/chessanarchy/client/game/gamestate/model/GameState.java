package com.wallissoftware.chessanarchy.client.game.gamestate.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

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

	public SystemOfGovernment getWhiteGovernment() {
		return SystemOfGovernment.get(getNativeWhiteGovernment());
	}

	public SystemOfGovernment getBlackGovernment() {
		return SystemOfGovernment.get(getNativeBlackGovernment());
	}

	public static GameState fromJson(final String json) {
		return JsonUtils.safeEval(json);
	}

	public native void addMove(String move) /*-{
		this.moveList.push(move);

	}-*/;

}
