package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.client.game.chat.model.Message;

public class MessageLogView extends ViewWithUiHandlers<MessageLogUiHandlers> implements MessageLogPresenter.MyView {
	public interface Binder extends UiBinder<Widget, MessageLogView> {
	}

	@UiField ScrollPanel scrollPanel;
	@UiField VerticalPanel messageLog;

	@Inject
	MessageLogView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void addMessage(final Message message) {
		final boolean isOnBottom = scrollPanel.getMaximumVerticalScrollPosition() - scrollPanel.getVerticalScrollPosition() < 50;

		final MessageWidget messageWidget = new MessageWidget(message);
		final int widgetCount = messageLog.getWidgetCount();
		if (widgetCount == 0) {
			messageLog.add(messageWidget);

		} else {
			int minVal = 0;
			int maxVal = widgetCount - 1;
			while (minVal < maxVal) {
				final int searchPos = ((maxVal - minVal) / 2) + minVal;
				final MessageWidget toCompare = (MessageWidget) messageLog.getWidget(searchPos);
				if (toCompare.getCreated() < message.getCreated()) {
					minVal = searchPos + 1;
				} else {
					maxVal = searchPos;
				}
			}
			final MessageWidget toCompare = (MessageWidget) messageLog.getWidget(maxVal);
			if (toCompare.getCreated() < message.getCreated()) {
				messageLog.insert(messageWidget, maxVal + 1);
			} else {
				messageLog.insert(messageWidget, maxVal);
			}

		}
		if (isOnBottom) {
			scrollPanel.scrollToBottom();
		} else {
			scrollPanel.setVerticalScrollPosition(scrollPanel.getVerticalScrollPosition() + messageWidget.getOffsetHeight());
		}

	}

	@Override
	public boolean isScrollBarShowing() {
		return scrollPanel.getMaximumVerticalScrollPosition() > 0;
		//return messageLog.getOffsetHeight() > scrollPanel.getOffsetHeight() - 100;
	}

	@UiHandler("scrollPanel")
	void onScrollPanelScroll(final ScrollEvent event) {
		if (scrollPanel.getVerticalScrollPosition() < 10) {
			getUiHandlers().prependEarlierMessages();
		}
	}
}
