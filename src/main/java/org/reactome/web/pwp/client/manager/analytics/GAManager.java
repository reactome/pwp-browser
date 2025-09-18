package org.reactome.web.pwp.client.manager.analytics;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import org.reactome.web.pwp.client.common.Selection;
import org.reactome.web.pwp.client.common.events.DatabaseObjectSelectedEvent;
import org.reactome.web.pwp.client.common.events.DetailsTabChangedEvent;
import org.reactome.web.pwp.client.common.events.StateChangedEvent;
import org.reactome.web.pwp.client.common.handlers.DatabaseObjectSelectedHandler;
import org.reactome.web.pwp.client.common.handlers.DetailsTabChangedHandler;
import org.reactome.web.pwp.client.common.handlers.StateChangedHandler;
import org.reactome.web.pwp.client.common.module.BrowserModule;
import org.reactome.web.pwp.client.common.utils.Console;
import org.reactome.web.pwp.client.manager.state.State;
import org.reactome.web.pwp.client.manager.state.token.Token;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Species;

import java.util.Objects;

/**
 * GAManager keeps track of those events that are of interest in order to get statistics of the web application.
 * <p>
 * A part from the "trackPageview" option that GA offers, we track some internal events that have been defined to
 * know how the users use the application. NOTE: trackPageview does not differentiate pages by token (string after #)
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GAManager implements BrowserModule.Manager,
        DatabaseObjectSelectedHandler, DetailsTabChangedHandler,
        StateChangedHandler {

    private static final String PREFIX = "\t\t[GAManager] ";

    private State currentState;

    private String gtagId;

    //Set to true in order to see in the console what the GAManager is doing
    private static final boolean TRACK_GA_MANAGER = true;
    private boolean gaTrackerActive;

    public GAManager(EventBus eventBus) {
        eventBus.addHandler(DatabaseObjectSelectedEvent.TYPE, this);
        eventBus.addHandler(DetailsTabChangedEvent.TYPE, this);
        eventBus.addHandler(StateChangedEvent.TYPE, this);

        LocationHelper.Location location = LocationHelper.getLocation();
        try {
            switch (location) {
                case PRODUCTION:
                    loadAnalytics("G-EDHZ92GXZP");
                    this.gaTrackerActive = true;
                    break;
                case DEV:
                    loadAnalytics("G-96F1EYHQR3");
                    this.gaTrackerActive = true;
                    break;
                case RELEASE:
                    loadAnalytics("G-ZCVRDTGMQJ");
                    this.gaTrackerActive = true;
                    break;
                case CURATOR:
                    loadAnalytics("G-4DC6WQ99L9");
                    this.gaTrackerActive = true;
                    break;
                default:
                    this.gaTrackerActive = false;
            }
        } catch (JavaScriptException ex) {
            this.gaTrackerActive = false;
        }

        if (!this.gaTrackerActive && TRACK_GA_MANAGER) {
            Console.info("[GAManager] set for DEV purposes");
        }
    }

    public void loadAnalytics(String gtagId) {
        this.gtagId = gtagId;
        ScriptElement gtagScript = Document.get().createScriptElement();
        gtagScript.setType("text/javascript");
        gtagScript.setSrc("https://www.googletagmanager.com/gtag/js?id=" + gtagId);
        gtagScript.setAttribute("async", "true");

        HeadElement head = Document.get().getElementsByTagName("head").getItem(0).cast();
        head.appendChild(gtagScript);


        //language=JavaScript
        String inline = ""
                + "window.dataLayer = window.dataLayer || [];"
                + "function gtag(){dataLayer.push(arguments);}"
                + "gtag('js', new Date());"
                + "gtag('config', '" + gtagId + "', { page_location: '" + getUrl() + "', page_referrer: '" + Document.get().getReferrer() + "', send_page_view: false, update:true });"
                + "gtag('event', 'page_view');";

        ScriptElement inlineScript = Document.get().createScriptElement(inline);
        inlineScript.setType("text/javascript");
        head.appendChild(inlineScript);
    }

    @Override
    public void onDatabaseObjectSelected(DatabaseObjectSelectedEvent event) {
        String module = event.getSource().getClass().getSimpleName();
        String action = "SELECTED";

        Selection selection = event.getSelection();
        if (selection != null) {
            if (!Objects.equals(selection.getDiagram(), currentState.getPathway())) {
                trackEvent(selection.getDiagram(), action, module);
            }
            if (!Objects.equals(selection.getDatabaseObject(), currentState.getSelected())) {
                trackEvent(selection.getDatabaseObject(), action, module);
            }
        }
    }

    @Override
    public void onDetailsTabChanged(DetailsTabChangedEvent event) {
        String module = event.getSource().getClass().getSimpleName();
        trackEvent(event.getDetailsTab().toString(), "SELECTED", module);
    }

    public static String getUrl() {
        return Window.Location.getHref().split("&")[0].replace("/#", "");
    }

    private String lastUrl = getUrl();

    @Override
    public void onStateChanged(final StateChangedEvent event) {
        Scheduler.get().scheduleDeferred(() -> {
            currentState = new State(event.getState());

            if (gaTrackerActive) {
                String url = getUrl();
                if (url.equals(lastUrl)) return;
                String title = getSimplifiedTitle();
                trackPageView(url, lastUrl, title, gtagId);
                if (TRACK_GA_MANAGER)
                    Console.info(PREFIX + "Event tracked: [ Page changed to : \"" + url + "\", title : \"" + title + "\" ]");
                lastUrl = url;
            }
        });
    }

    private String getSimplifiedTitle() {
        StringBuilder title = new StringBuilder();
        if (currentState.getPathway() != null) title.append("PB | ").append(currentState.getPathway().getDisplayName());
        else title.append("Reactome | PathwayBrowser");

        Species species = currentState.getSpecies();
        if (species != null && !species.getDbId().equals(Token.DEFAULT_SPECIES_ID)) {
            title.append(" [").append(species.getDisplayName()).append("]");
        }
        return title.toString();
    }


    private void trackEvent(DatabaseObject element, String action, String module) {
        if (element == null) return;
        String category = element.getSchemaClass().toString();
        trackEvent(category, action, module);
    }

    private void trackEvent(String category, String action, String module) {
        if (category == null || action == null || module == null) return;

        category = category.toUpperCase();
        module = module.replace("Presenter", "").replace("TAB", "_TAB").toUpperCase();
        action = action.toUpperCase();

        if (gaTrackerActive) {
            sendEvent(category, action, module);
        }
        if (TRACK_GA_MANAGER) {
            Console.info(PREFIX + "Event tracked: [" + category + ", " + action + ", " + module + "]");
        }
    }

    // --- Helper to manually send page_view (SPA navigation) ---

    public static native void trackPageView(String url, String lastUrl, String title, String gtagId) /*-{
        $wnd.gtag('config', gtagId, {
            page_location: url,
            page_referrer: lastUrl,
            page_title: title,
            send_page_view: false,
            update: true
        });
        $wnd.gtag('event', 'page_view');
    }-*/;
    // Generic custom event sender

    public static native void sendEvent(String category, String action, String module) /*-{
        $wnd.gtag('event', action, {content_group: module, content_type: category});
    }-*/;

}
