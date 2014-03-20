package com.wallissoftware.chessanarchy.client.game.movetodraw;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class MoveToDrawModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindSingletonPresenterWidget(MoveToDrawPresenter.class, MoveToDrawPresenter.MyView.class, MoveToDrawView.class);
    }
}
