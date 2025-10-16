package org.reactome.web.pwp.client.toppanel.beta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
public class BetaDisplay extends Composite implements Beta.Display, ClickHandler {

    private Beta.Presenter presenter;

    private IconButton button;

    public BetaDisplay() {
        button = new IconButton("", RESOURCES.betaIcon());
        button.setTitle("Open beta version of the current page");
        button.setStyleName(RESOURCES.getCSS().betaButton());
        button.addClickHandler(this);

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setStyleName(RESOURCES.getCSS().betaPanel());
        flowPanel.add(new SimplePanel(new InlineLabel("Beta Browser:")));

        flowPanel.add(this.button);

        initWidget(flowPanel);
    }

    @Override
    public void setPresenter(Beta.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(ClickEvent event) {
        presenter.openBeta();
    }

    private boolean isAnimationActive = false;
    public void triggerAnimation() {
        if (isAnimationActive) return;
        // Add class again to trigger animation
        isAnimationActive = true;
        button.addStyleName(RESOURCES.getCSS().animate());
        button.getOffsetWidth(); // Force re-layout
        Scheduler.get().scheduleFixedDelay(() -> {
            button.removeStyleName(RESOURCES.getCSS().animate());
            isAnimationActive = false;
            return false;
        }, 1000);
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

        @Source("images/update.png")
        ImageResource betaIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("pwp-LayoutSelector")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/pwp/client/toppanel/beta/Beta.css";

        String betaPanel();

        String betaButton();

        String animate();

    }
}
