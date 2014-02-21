package com.wallissoftware.chessanarchy.client.game.chat.messageinput;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class MessageInputView extends ViewWithUiHandlers<MessageInputUiHandlers> implements MessageInputPresenter.MyView {
	public interface Binder extends UiBinder<Widget, MessageInputView> {
	}

	@UiField HasText messageBox;

	@Inject
	MessageInputView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@UiHandler("sendButton")
	void onSendButtonClick(final ClickEvent event) {
		getUiHandlers().sendMessage(messageBox.getText());
	}

	@UiHandler("messageBox")
	void onMessageBoxKeyUp(final KeyUpEvent event) {
		if (!event.isShiftKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			getUiHandlers().sendMessage(messageBox.getText());
			messageBox.setText("");
		}
	}
}
