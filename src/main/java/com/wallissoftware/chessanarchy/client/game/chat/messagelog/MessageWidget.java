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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public class MessageWidget extends Composite {

	private static MessageWidgetUiBinder uiBinder = GWT.create(MessageWidgetUiBinder.class);

	interface MessageWidgetUiBinder extends UiBinder<Widget, MessageWidget> {
	}

	interface MyStyle extends CssResource {
		String command();

		String gameMaster();

		String black();

		String white();

		String ownMessage();
	}

	@UiField MyStyle style;

	@UiField(provided = true) InlineLabel message, creation, name;
	@UiField SimplePanel color;
	private final double creationTime;

	public MessageWidget(final MessageWrapper message) {

		this.message = new InlineLabel(message.getFormattedMessage());
		this.name = new InlineLabel("<" + message.getName() + ">");

		this.creation = new InlineLabel(DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM).format(new Date(message.getCreated())));
		this.creationTime = message.getCreated();
		initWidget(uiBinder.createAndBindUi(this));
		if (message.is3rdPerson() || message.isNickChange() || message.isTeamChange() || message.isFromGameMaster()) {
			name.setVisible(false);

			if (message.isFromGameMaster()) {
				this.message.addStyleName(style.gameMaster());
			} else {
				this.message.addStyleName(style.command());
			}
		} else {
			try {
				name.getElement().getStyle().setColor(intToColor(getHash(message.getUserId())));
			} catch (final Exception e) {

			}
		}
		if (message.getColor() != null) {
			this.color.addStyleName(message.getColor() == Color.WHITE ? style.white() : style.black());
		}
		if (message.getUserId().equals(User.get().getUserId())) {
			this.addStyleName(style.ownMessage());
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
