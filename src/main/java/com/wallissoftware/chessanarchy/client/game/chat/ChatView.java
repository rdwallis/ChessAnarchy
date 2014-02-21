package com.wallissoftware.chessanarchy.client.game.chat;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class ChatView extends ViewImpl implements ChatPresenter.MyView {
	public interface Binder extends UiBinder<Widget, ChatView> {
	}

	@UiField HasOneWidget messageLogPanel, messageInputPanel;

	@Inject
	ChatView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot == ChatPresenter.MESSAGE_INPUT_SLOT) {
			messageInputPanel.setWidget(content);
		} else if (slot == ChatPresenter.MESSAGE_LOG_SLOT) {
			messageLogPanel.setWidget(content);
		} else {
			super.setInSlot(slot, content);
		}
	}

}
