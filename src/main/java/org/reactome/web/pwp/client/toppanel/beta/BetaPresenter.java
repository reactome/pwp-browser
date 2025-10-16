package org.reactome.web.pwp.client.toppanel.beta;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import org.reactome.web.pwp.client.common.events.StateChangedEvent;
import org.reactome.web.pwp.client.common.module.AbstractPresenter;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class BetaPresenter extends AbstractPresenter implements Beta.Presenter {

    private Beta.Display display;

    public BetaPresenter(EventBus eventBus, Beta.Display display) {
        super(eventBus);
        this.display = display;
        this.display.setPresenter(this);

        eventBus.addHandler(StateChangedEvent.TYPE, this);
    }

    @Override
    public void onStateChanged(StateChangedEvent event) {
        display.triggerAnimation();
    }

    @Override
    public void openBeta() {
        Window.Location.assign(Window.Location.getHref().replace("/PathwayBrowser", "/beta/PathwayBrowser"));
    }
}
