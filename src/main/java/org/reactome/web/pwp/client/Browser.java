package org.reactome.web.pwp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import org.reactome.web.fireworks.client.FireworksFactory;
import org.reactome.web.pwp.client.common.utils.Console;
import org.reactome.web.pwp.client.manager.state.token.Token;
import org.reactome.web.pwp.client.manager.title.event.TitleChangedEvent;
import org.reactome.web.pwp.model.client.classes.DBInfo;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;

import java.util.Arrays;
import java.util.List;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Browser implements EntryPoint, ContentClientHandler.DatabaseInfo {

    public static final String VERSION = "3.7";
    public static final Boolean BETA = false;
    
    //set both of these to override switch case in GAManager. Otherwise, switch case
    //will proceed as normal
    public static String GA_TOKEN = null;
    public static String GA_DOMAIN = null;

    public static boolean VERBOSE = true;
    private AppController appViewer;

    private List<String> scriptsToLoad = Arrays.asList(
//            "https://www.ebi.ac.uk/gxa/resources/js-bundles/vendorCommons.bundle.js",
//            "https://www.ebi.ac.uk/gxa/resources/js-bundles/expressionAtlasHeatmapHighcharts.bundle.js"
    );

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        initConfig();
        ContentClient.getDatabaseInformation(this);
        loadScripts();
    }

    @Override
    public void onDatabaseInfoLoaded(DBInfo dbInfo) {
        Scheduler.get().scheduleDeferred(() -> {
            appViewer = new AppController(dbInfo);
            appViewer.go(RootLayoutPanel.get());
            removeLoadingMessage();
        });
    }

    @Override
    public void onContentClientException(Type type, String message) {
        showErrorMessage();
    }

    @Override
    public void onContentClientError(ContentClientError error) {
        showErrorMessage();
    }

    protected void initConfig() {
        String hostName = Window.Location.getHostName();
        Browser.VERBOSE = (hostName.equals("localhost") || hostName.equals("127.0.0.1"));
        TitleChangedEvent.REPORT = false;
        Token.DEFAULT_SPECIES_ID = 48887L; //Homo sapiens
        Token.DELIMITER = "&";
        FireworksFactory.SHOW_FOAM_BTN = true;
    }

    private void removeLoadingMessage() {
        try {
            if (DOM.getElementById("appLoadMessage") != null) {
                DOM.getElementById("appLoadMessage").removeFromParent();
            }
        } catch (JavaScriptException exception) {
            Console.error(exception.getMessage(), this);
        }
    }

    private void showErrorMessage() {
        removeLoadingMessage();
        try {
            if (DOM.getElementById("appErrorMessage") != null) {
                DOM.getElementById("appErrorMessage").getStyle().setDisplay(Style.Display.BLOCK);
            }
        } catch (JavaScriptException exception) {
            Console.error(exception.getMessage(), this);
        }
    }

    private void loadScripts() {
        for (String url : scriptsToLoad) {
            ScriptInjector.fromUrl(url)
                          .setRemoveTag(false)
                          .setWindow(ScriptInjector.TOP_WINDOW)
                          .inject();
        }
    }
}
