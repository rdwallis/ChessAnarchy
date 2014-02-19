package com.wallissoftware.client.application.widget;

import com.wallissoftware.client.application.widget.header.HeaderModule;
import com.wallissoftware.client.application.widget.login.LoginModule;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class WidgetModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new HeaderModule());
        install(new LoginModule());
    }
}
