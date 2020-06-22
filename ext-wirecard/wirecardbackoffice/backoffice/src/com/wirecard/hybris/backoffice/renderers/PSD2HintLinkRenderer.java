package com.wirecard.hybris.backoffice.renderers;

import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import de.hybris.platform.util.Config;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.A;

public class PSD2HintLinkRenderer implements WidgetComponentRenderer {

	@Override
	public void render(final Object parent, final Object config, final Object data,
	                   final DataType type, final WidgetInstanceManager manager) {
		final String url = Config.getParameter("wirecard.psd2hint.link"); //defined in project.properties
		final A link = new A(url);
		link.setHref(url);
		link.setTarget("_blank");

		final Component component = (Component) parent;
		component.appendChild(link);
	}
}
