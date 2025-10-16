package org.reactome.web.pwp.client.toppanel.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.reactome.web.pwp.client.details.common.widgets.button.IconButton;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LayoutSelectorDisplay extends Composite implements LayoutSelector.Display, ClickHandler {

    private LayoutSelector.Presenter presenter;

    private IconButton hierarchy;
    private IconButton details;
    private IconButton viewport;

    public LayoutSelectorDisplay() {
        hierarchy = new IconButton("", RESOURCES.layoutIcon());
        hierarchy.setTitle("show/hide hierarchy panel");
        hierarchy.setStyleName(RESOURCES.getCSS().hierarchyBtn());
        hierarchy.addClickHandler(this);

        details = new IconButton("", RESOURCES.layoutIcon());
        details.setTitle("show/hide details panel");
        details.setStyleName(RESOURCES.getCSS().detailsBtn());
        details.addClickHandler(this);

        viewport = new IconButton("", RESOURCES.layoutIcon());
        viewport.setTitle("expand/minimise centre display");
        viewport.setStyleName(RESOURCES.getCSS().centreBtn());
        viewport.addClickHandler(this);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setStyleName(RESOURCES.getCSS().layoutPanel());
        flowPanel.add(new SimplePanel(new InlineLabel("Layout:")));
        flowPanel.add(this.hierarchy);
        flowPanel.add(this.details);
        flowPanel.add(this.viewport);

        initWidget(flowPanel);
    }

    @Override
    public void setPresenter(LayoutSelector.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(ClickEvent event) {
        IconButton btn = (IconButton) event.getSource();
        if (btn == this.hierarchy) {
            this.presenter.layoutSelectorChanged(LayoutSelectorType.HIERARCHY);
        } else if (btn == this.details) {
            this.presenter.layoutSelectorChanged(LayoutSelectorType.DETAILS);
        } else if (btn == this.viewport ) {
            this.presenter.layoutSelectorChanged(LayoutSelectorType.VIEWPORT);
        }
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/layout.png")
        ImageResource layoutIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("pwp-LayoutSelector")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/pwp/client/toppanel/layout/Layout.css";

        String layoutPanel();

        String hierarchyBtn();

        String detailsBtn();

        String centreBtn();

    }
}
