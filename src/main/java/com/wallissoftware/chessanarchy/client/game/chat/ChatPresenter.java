package com.wallissoftware.chessanarchy.client.game.chat;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.messageinput.MessageInputPresenter;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogPresenter;

public class ChatPresenter extends PresenterWidget<ChatPresenter.MyView> {
	public interface MyView extends View {
	}

	public final static Object MESSAGE_INPUT_SLOT = new Object();
	public final static Object MESSAGE_LOG_SLOT = new Object();
	private final MessageLogPresenter messageLogPresenter;
	private final MessageInputPresenter messageInputPresenter;

	@Inject
	ChatPresenter(final EventBus eventBus, final MyView view, final MessageInputPresenter messageInputPresenter, final MessageLogPresenter messageLogPresenter) {
		super(eventBus, view);
		this.messageInputPresenter = messageInputPresenter;
		this.messageLogPresenter = messageLogPresenter;
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(MESSAGE_INPUT_SLOT, messageInputPresenter);
		setInSlot(MESSAGE_LOG_SLOT, messageLogPresenter);
	}

}
