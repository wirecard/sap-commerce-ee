package com.wirecard.hybris.backoffice.renderers;

import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;
import org.zkoss.zhtml.Br;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;

public class PSD2HintRenderer implements WidgetComponentRenderer {
	private static final String URL_KEY = "wirecard.psd2hint.link";
	private static final String DESCRIPTION_KEY = "type.PSD2HintRenderer.description";

	@Override
	public void render(final Object parent, final Object config, final Object data,
	                   final DataType type, final WidgetInstanceManager manager) {
		final Label label = new Label();
		label.setValue(Localization.getLocalizedString(DESCRIPTION_KEY));

		final String url = Config.getParameter(URL_KEY); //defined in project.properties
		final A link = new A(url);
		link.setHref(url);
		link.setTarget("_blank");

		final Component component = (Component) parent;
		component.appendChild(label);
		component.appendChild(new Br());
		component.appendChild(link);
	}
}
