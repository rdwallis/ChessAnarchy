package com.wallissoftware.chessanarchy.client.game.chat.messageinput;

import com.gwtplatform.mvp.client.UiHandlers;

public interface MessageInputUiHandlers extends UiHandlers {

	void sendMessage(String message, boolean resendOnFail);
}
