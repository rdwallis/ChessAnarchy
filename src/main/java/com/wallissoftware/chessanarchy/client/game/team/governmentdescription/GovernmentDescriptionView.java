package com.wallissoftware.chessanarchy.client.game.team.governmentdescription;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class GovernmentDescriptionView extends PopupViewWithUiHandlers<GovernmentDescriptionUiHandlers> implements GovernmentDescriptionPresenter.MyView {
	public interface Binder extends UiBinder<DialogBox, GovernmentDescriptionView> {
	}

	@UiField DialogBox dialogBox;

	@UiField HasText description;

	@Inject
	GovernmentDescriptionView(final Binder uiBinder, final EventBus eventBus) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setGovernmentName(final String name) {
		dialogBox.setText(name);

	}

	@Override
	public void setDescription(final String description) {
		this.description.setText(description);

	}

	@UiHandler("close")
	void onCloseButtonClick(final ClickEvent click) {
		hide();
	}

	@Override
	public void showRelativeTo(final IsWidget widget) {
		if (asPopupPanel().isShowing()) {
			hide();
		} else {
			asPopupPanel().showRelativeTo(widget.asWidget());
		}

	}

	@Override
	public void addAutoHidePartner(final IsWidget view) {
		asPopupPanel().addAutoHidePartner(view.asWidget().getElement());

	}
}
