package com.wallissoftware.chessanarchy.shared.message;

import com.wallissoftware.chessanarchy.shared.dispatch.DefaultActionImpl;

public class SendMessageAction extends DefaultActionImpl<SendMessageResult> {

	private String message;

	@SuppressWarnings("unused")
	private SendMessageAction() {
	};

	public SendMessageAction(final String message) {
		this.message = message;
	};

	public String getMessage() {
		return message;
	}

	@Override
	public boolean isSecured() {
		return true;
	}

}
