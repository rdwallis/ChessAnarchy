package com.wallissoftware.chessanarchy.client.game.team.governmentdescription;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.wallissoftware.chessanarchy.shared.governments.GovernmentInfo;

public class GovernmentDescriptionPresenter extends PresenterWidget<GovernmentDescriptionPresenter.MyView> implements GovernmentDescriptionUiHandlers {
	public interface MyView extends PopupView, HasUiHandlers<GovernmentDescriptionUiHandlers> {

		void setGovernmentName(String name);

		void setDescription(String description);

		void showRelativeTo(IsWidget view);

		void addAutoHidePartner(IsWidget view);
	}

	@Inject
	GovernmentDescriptionPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);

		getView().setUiHandlers(this);
	}

	public void setGovernment(final GovernmentInfo governmentInfo) {
		getView().setGovernmentName(governmentInfo.getName());
		getView().setDescription(governmentInfo.getDescription());
	}

	public void showRelativeTo(final IsWidget view) {
		getView().showRelativeTo(view);
	}

	public void addAutoHidePartner(final IsWidget view) {
		getView().addAutoHidePartner(view);
	}

}
