package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.shared.CAConstants;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public class MessageLogView extends ViewWithUiHandlers<MessageLogUiHandlers> implements MessageLogPresenter.MyView {
    public interface Binder extends UiBinder<Widget, MessageLogView> {
    }

    @UiField ScrollPanel scrollPanel;
    @UiField VerticalPanel messageLog;

    private final Map<String, MessageWidget> limboMessages = new HashMap<String, MessageWidget>();

    private final List<MessageWrapper> messageQueue = new ArrayList<MessageWrapper>();

    @Inject
    MessageLogView(final Binder binder) {
        initWidget(binder.createAndBindUi(this));

        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

            @Override
            public boolean execute() {
                processMessageQueue();
                return true;
            }
        }, 100);
    }

    private void processMessageQueue() {
        final Iterator<MessageWrapper> it = messageQueue.iterator();
        while (it.hasNext()) {
            final MessageWrapper next = it.next();
            if (SyncedTime.get() - next.getCreated() > CAConstants.SYNC_DELAY) {
                addMessage(next, true);
                it.remove();
            }
        }

        final Iterator<Entry<String, MessageWidget>> lmIt = limboMessages.entrySet().iterator();
        while (lmIt.hasNext()) {
            if (SyncedTime.get() - lmIt.next().getValue().getCreated() > 10000) {
                lmIt.remove();
            }
        }

    }

    @Override
    public void addMessage(final MessageWrapper message, final boolean force) {
        if (force || SyncedTime.get() - message.getCreated() > CAConstants.SYNC_DELAY) {
            final boolean isOnBottom = scrollPanel.getMaximumVerticalScrollPosition() - scrollPanel.getVerticalScrollPosition() < 50;

            final MessageWidget messageWidget = new MessageWidget(message);
            if (SyncedTime.get() - message.getCreated() < CAConstants.SYNC_DELAY) {
                limboMessages.put(message.getId(), messageWidget);
            }
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
        } else {
            messageQueue.add(message);
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

    @Override
    public void removeLimboMessage(final String messageId) {
        if (limboMessages.containsKey(messageId)) {
            messageLog.remove(limboMessages.get(messageId));
        }

    }
}
