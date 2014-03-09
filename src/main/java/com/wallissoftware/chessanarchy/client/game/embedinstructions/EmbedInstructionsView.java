package com.wallissoftware.chessanarchy.client.game.embedinstructions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;

public class EmbedInstructionsView extends PopupViewImpl implements EmbedInstructionsPresenter.MyView {
	public interface Binder extends UiBinder<DialogBox, EmbedInstructionsView> {
	}

	@Inject
	EmbedInstructionsView(final Binder uiBinder, final EventBus eventBus) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("closeButton")
	void onCloseButtonClick(final ClickEvent event) {
		hide();
	}
}
