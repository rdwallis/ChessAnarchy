package com.wallissoftware.chessanarchy.client.game.team;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface HelpIcons extends ClientBundle {

	public static final HelpIcons INSTANCE = GWT.create(HelpIcons.class);

	ImageResource help_black();

	ImageResource help_white();

}
