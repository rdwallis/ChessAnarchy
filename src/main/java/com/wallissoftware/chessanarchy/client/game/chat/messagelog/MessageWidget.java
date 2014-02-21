package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.wallissoftware.chessanarchy.client.game.chat.model.Message;

public class MessageWidget extends Composite {

	private static MessageWidgetUiBinder uiBinder = GWT.create(MessageWidgetUiBinder.class);

	interface MessageWidgetUiBinder extends UiBinder<Widget, MessageWidget> {
	}

	@UiField(provided = true) Label message, creation, name;
	private final double creationTime;

	public MessageWidget(final Message message) {
		this.message = new Label(message.getMessage());
		this.name = new Label(message.getName());
		this.creation = new Label(DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM).format(new Date((long) message.getCreated())));
		this.creationTime = message.getCreated();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public double getCreated() {
		return creationTime;
	}

}
