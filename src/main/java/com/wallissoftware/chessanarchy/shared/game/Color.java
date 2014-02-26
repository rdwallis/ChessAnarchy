package com.wallissoftware.chessanarchy.shared.game;

public enum Color {
	BLACK, WHITE;

	public Color getOpposite() {
		if (this == WHITE) {
			return BLACK;
		} else {
			return WHITE;
		}
	}
}
