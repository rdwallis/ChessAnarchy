package com.wallissoftware.client.game;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.RootPresenter;

public class MyRootPresenter extends RootPresenter {

	public static final class MyRootView extends RootView {
		@Override
		public void setInSlot(final Object slot, final IsWidget widget) {
			RootPanel.get("mainContent").add(widget);
		}
	}

	@Inject
	MyRootPresenter(final EventBus eventBus, final MyRootView myRootView) {
		super(eventBus, myRootView);
	}
}