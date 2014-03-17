package com.wallissoftware.chessanarchy.client.game.election;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class ElectionView extends PopupViewWithUiHandlers<ElectionUiHandlers> implements ElectionPresenter.MyView {
	public interface Binder extends UiBinder<PopupPanel, ElectionView> {
	}

	@Inject
	ElectionView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
	}
}
