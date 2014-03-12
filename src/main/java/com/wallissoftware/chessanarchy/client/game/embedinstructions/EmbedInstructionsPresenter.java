package com.wallissoftware.chessanarchy.client.game.embedinstructions;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class EmbedInstructionsPresenter extends PresenterWidget<EmbedInstructionsPresenter.MyView> {
	public interface MyView extends PopupView {
	}

	@Inject
	EmbedInstructionsPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);

	}

}
