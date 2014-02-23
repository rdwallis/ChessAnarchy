package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.wallissoftware.chessanarchy.client.game.chat.model.Message;

public class MessageWidget extends Composite {

	private static MessageWidgetUiBinder uiBinder = GWT.create(MessageWidgetUiBinder.class);

	interface MessageWidgetUiBinder extends UiBinder<Widget, MessageWidget> {
	}

	interface MyStyle extends CssResource {
		String command();
	}

	@UiField MyStyle style;

	@UiField(provided = true) InlineLabel message, creation, name;
	private final double creationTime;

	public MessageWidget(final Message message) {

		this.message = new InlineLabel(message.getFormattedMessage());
		this.name = new InlineLabel("<" + message.getName() + ">");

		this.creation = new InlineLabel(DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM).format(new Date((long) message.getCreated())));
		this.creationTime = message.getCreated();
		initWidget(uiBinder.createAndBindUi(this));
		if (message.is3rdPerson() || message.isNickChange()) {
			name.setVisible(false);
			this.message.addStyleName(style.command());
		} else {
			name.getElement().getStyle().setColor(intToColor(getHash(message.getName())));
		}
	}

	public double getCreated() {
		return creationTime;
	}

	private final native double getHash(String input) /*-{
		var hash = 0;
		if (this.length == 0) {
			return hash;
		}
		for (var i = 0; i < input.length; i++) {
			hash = input.charCodeAt(i) + ((hash << 5) - hash);
			hash = hash & hash; // Convert to 32bit integer
		}
		return hash;
	}-*/;

	private final native String intToColor(double input)/*-{

		var shortened = input % 360;
		return "hsl(" + shortened + ",100%,30%)";
	}-*/;

}
