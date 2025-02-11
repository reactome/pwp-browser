package org.reactome.web.pwp.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import org.reactome.web.pwp.client.common.BrowserEventBus;
import org.reactome.web.pwp.client.common.events.BrowserReadyEvent;
import org.reactome.web.pwp.client.common.handlers.BrowserReadyHandler;
import org.reactome.web.pwp.client.common.utils.Console;
import org.reactome.web.pwp.client.details.Details;
import org.reactome.web.pwp.client.details.DetailsDisplay;
import org.reactome.web.pwp.client.details.DetailsPresenter;
import org.reactome.web.pwp.client.details.tabs.DetailsTab;
import org.reactome.web.pwp.client.details.tabs.analysis.AnalysisTab;
import org.reactome.web.pwp.client.details.tabs.analysis.AnalysisTabDisplay;
import org.reactome.web.pwp.client.details.tabs.analysis.AnalysisTabPresenter;
import org.reactome.web.pwp.client.details.tabs.description.DescriptionTab;
import org.reactome.web.pwp.client.details.tabs.description.DescriptionTabDisplay;
import org.reactome.web.pwp.client.details.tabs.description.DescriptionTabPresenter;
import org.reactome.web.pwp.client.details.tabs.downloads.DownloadsTab;
import org.reactome.web.pwp.client.details.tabs.downloads.DownloadsTabDisplay;
import org.reactome.web.pwp.client.details.tabs.downloads.DownloadsTabPresenter;
import org.reactome.web.pwp.client.details.tabs.expression.ExpressionTab;
import org.reactome.web.pwp.client.details.tabs.expression.ExpressionTabDisplay;
import org.reactome.web.pwp.client.details.tabs.expression.ExpressionTabPresenter;
import org.reactome.web.pwp.client.details.tabs.molecules.MoleculesTab;
import org.reactome.web.pwp.client.details.tabs.molecules.MoleculesTabDisplay;
import org.reactome.web.pwp.client.details.tabs.molecules.MoleculesTabPresenter;
import org.reactome.web.pwp.client.details.tabs.structures.StructuresTab;
import org.reactome.web.pwp.client.details.tabs.structures.StructuresTabDisplay;
import org.reactome.web.pwp.client.details.tabs.structures.StructuresTabPresenter;
import org.reactome.web.pwp.client.hierarchy.Hierarchy;
import org.reactome.web.pwp.client.hierarchy.HierarchyDisplay;
import org.reactome.web.pwp.client.hierarchy.HierarchyPresenter;
import org.reactome.web.pwp.client.main.DesktopApp;
import org.reactome.web.pwp.client.main.DesktopAppDisplay;
import org.reactome.web.pwp.client.main.DesktopAppPresenter;
import org.reactome.web.pwp.client.manager.analytics.GAManager;
import org.reactome.web.pwp.client.manager.orthology.OrthologyManager;
import org.reactome.web.pwp.client.manager.state.StateManager;
import org.reactome.web.pwp.client.messages.Messages;
import org.reactome.web.pwp.client.messages.MessagesDisplay;
import org.reactome.web.pwp.client.messages.MessagesPresenter;
import org.reactome.web.pwp.client.tools.analysis.AnalysisLauncher;
import org.reactome.web.pwp.client.tools.analysis.AnalysisLauncherDisplay;
import org.reactome.web.pwp.client.tools.analysis.AnalysisLauncherPresenter;
import org.reactome.web.pwp.client.tools.citation.CitationLauncher;
import org.reactome.web.pwp.client.tools.citation.CitationLauncherDisplay;
import org.reactome.web.pwp.client.tools.citation.CitationLauncherPresenter;
import org.reactome.web.pwp.client.tools.launcher.ToolLauncher;
import org.reactome.web.pwp.client.tools.launcher.ToolLauncherDisplay;
import org.reactome.web.pwp.client.tools.launcher.ToolLauncherPresenter;
import org.reactome.web.pwp.client.toppanel.layout.LayoutSelector;
import org.reactome.web.pwp.client.toppanel.layout.LayoutSelectorDisplay;
import org.reactome.web.pwp.client.toppanel.layout.LayoutSelectorPresenter;
import org.reactome.web.pwp.client.toppanel.logo.LogoPanel;
import org.reactome.web.pwp.client.toppanel.species.SpeciesSelector;
import org.reactome.web.pwp.client.toppanel.species.SpeciesSelectorDisplay;
import org.reactome.web.pwp.client.toppanel.species.SpeciesSelectorPresenter;
import org.reactome.web.pwp.client.toppanel.tour.TourSelector;
import org.reactome.web.pwp.client.toppanel.tour.TourSelectorDisplay;
import org.reactome.web.pwp.client.toppanel.tour.TourSelectorPresenter;
import org.reactome.web.pwp.client.viewport.Viewport;
import org.reactome.web.pwp.client.viewport.ViewportDisplay;
import org.reactome.web.pwp.client.viewport.ViewportPresenter;
import org.reactome.web.pwp.client.viewport.diagram.Diagram;
import org.reactome.web.pwp.client.viewport.diagram.DiagramDisplay;
import org.reactome.web.pwp.client.viewport.diagram.DiagramPresenter;
import org.reactome.web.pwp.client.viewport.fireworks.Fireworks;
import org.reactome.web.pwp.client.viewport.fireworks.FireworksDisplay;
import org.reactome.web.pwp.client.viewport.fireworks.FireworksPresenter;
import org.reactome.web.pwp.client.viewport.welcome.Welcome;
import org.reactome.web.pwp.client.viewport.welcome.WelcomeDisplay;
import org.reactome.web.pwp.client.viewport.welcome.WelcomePresenter;
import org.reactome.web.pwp.model.client.classes.DBInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AppController implements BrowserReadyHandler {

    public static EventBus eventBus;
    private final DBInfo dbInfo;
    private final IsWidget main;

    public AppController(DBInfo dbInfo) {
        this.dbInfo = dbInfo;
        this.printMessage();
        AppController.eventBus = new BrowserEventBus();
        AppController.eventBus.addHandler(BrowserReadyEvent.TYPE, this);
        this.initManager();
        this.main = getDesktopBrowser();
    }

    public void go(HasWidgets container) {
        container.add(this.main.asWidget());
        eventBus.fireEventFromSource(new BrowserReadyEvent(dbInfo), this);
    }

    @Override
    public void onBrowserReady(BrowserReadyEvent event) {
        History.fireCurrentHistoryState();
    }

    private void initManager() {
        new StateManager(eventBus);
        new OrthologyManager(eventBus);
        new GAManager(eventBus);
    }

    private IsWidget getDesktopBrowser() {
        this.initialiseDetailsTabsList(); //IMPORTANT: It has to be initialised before creating the main details module
        this.initialiseTools(); //IMPORTANT: Even though the tools are not attached here, they need to be initialised

        //Messages is not attached anywhere here but it needs to be initialised
        Messages.Display messages = new MessagesDisplay();
        new MessagesPresenter(eventBus, messages);

        Hierarchy.Display hierarchy = new HierarchyDisplay();
        new HierarchyPresenter(eventBus, hierarchy);

        Details.Display details = new DetailsDisplay();
        new DetailsPresenter(eventBus, details);

        DesktopApp.Display main = new DesktopAppDisplay(getTopPanel(), hierarchy, details, getViewport());
        new DesktopAppPresenter(eventBus, main);

        return main;
    }

    public static final List<DetailsTab.Display> DETAILS_TABS = new LinkedList<>();

    private IsWidget getTopPanel() {
        LayoutSelector.Display layoutSelector = new LayoutSelectorDisplay();
        new LayoutSelectorPresenter(eventBus, layoutSelector);

        ToolLauncher.Display toolLauncher = new ToolLauncherDisplay();
        new ToolLauncherPresenter(eventBus, toolLauncher);

        SpeciesSelector.Display species = new SpeciesSelectorDisplay();
        new SpeciesSelectorPresenter(eventBus, species);

        TourSelector.Display tour = new TourSelectorDisplay();
        new TourSelectorPresenter(eventBus, tour);

        FlowPanel topPanel = new FlowPanel();
        topPanel.setStyleName("elv-Top-Panel");
        topPanel.add(new LogoPanel(dbInfo));
        topPanel.add(species);
        topPanel.add(layoutSelector);
        topPanel.add(tour);
        topPanel.add(toolLauncher);

        return topPanel;
    }

    private IsWidget getViewport() {
        Diagram.Display diagram = new DiagramDisplay();
        new DiagramPresenter(eventBus, diagram);

        Viewport.Display viewport;
        if (AppConfig.getIsCurator()) {
            Welcome.Display welcome = new WelcomeDisplay();
            new WelcomePresenter(eventBus, welcome);

            viewport = new ViewportDisplay(diagram, welcome);
        } else {
            Fireworks.Display fireworks = new FireworksDisplay();
            new FireworksPresenter(eventBus, fireworks);

            viewport = new ViewportDisplay(diagram, fireworks);
        }
        new ViewportPresenter(eventBus, viewport);
        return viewport;
    }

    private void initialiseDetailsTabsList() {
        DescriptionTab.Display description = new DescriptionTabDisplay();
        new DescriptionTabPresenter(eventBus, description);
        DETAILS_TABS.add(description);

        MoleculesTab.Display molecules = new MoleculesTabDisplay();
        new MoleculesTabPresenter(eventBus, molecules);
        DETAILS_TABS.add(molecules);

        StructuresTab.Display structures = new StructuresTabDisplay();
        new StructuresTabPresenter(eventBus, structures);
        DETAILS_TABS.add(structures);

        ExpressionTab.Display expression = new ExpressionTabDisplay();
        new ExpressionTabPresenter(eventBus, expression);
        DETAILS_TABS.add(expression);

        AnalysisTab.Display analysis = new AnalysisTabDisplay();
        new AnalysisTabPresenter(eventBus, analysis);
        DETAILS_TABS.add(analysis);

        DownloadsTab.Display downloads = new DownloadsTabDisplay();
        new DownloadsTabPresenter(eventBus, downloads);
        DETAILS_TABS.add(downloads);
    }

    private void initialiseTools() {
        AnalysisLauncher.Display analysisDisplay = new AnalysisLauncherDisplay();
        new AnalysisLauncherPresenter(eventBus, analysisDisplay);

        CitationLauncher.Display citationDisplay = new CitationLauncherDisplay();
        new CitationLauncherPresenter(eventBus, citationDisplay);
    }

    private void printMessage() {
        System.out.println();                             //This is in purpose for the java console
        System.out.println("##########################"); //This is in purpose for the java console
        Console.info("# Starting PathwayPortal #"); //Both java and browser console
        System.out.println("##########################"); //This is in purpose for the java console
    }
}
