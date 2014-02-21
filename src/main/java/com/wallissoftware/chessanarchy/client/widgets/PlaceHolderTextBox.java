package com.wallissoftware.chessanarchy.client.widgets;

import com.google.gwt.user.client.ui.TextBox;

public class PlaceHolderTextBox extends TextBox {

	public void setPlaceHolder(final String placeHolder) {
		this.getElement().setPropertyString("placeholder", placeHolder);
	}

}
