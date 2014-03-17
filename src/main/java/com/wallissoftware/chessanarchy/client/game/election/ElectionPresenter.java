package com.wallissoftware.chessanarchy.client.game.election;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class ElectionPresenter extends PresenterWidget<ElectionPresenter.MyView> implements ElectionUiHandlers {
	public interface MyView extends PopupView, HasUiHandlers<ElectionUiHandlers> {
	}

	@Inject
	ElectionPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);

		getView().setUiHandlers(this);
	}

}
