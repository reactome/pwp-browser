package org.reactome.web.pwp.client.tools.launcher;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.analysis.client.AnalysisClient;
import org.reactome.web.analysis.client.AnalysisHandler;
import org.reactome.web.analysis.client.model.AnalysisError;
import org.reactome.web.analysis.client.model.DBInfo;
import org.reactome.web.pwp.client.common.PathwayPortalTool;
import org.reactome.web.pwp.client.common.events.StateChangedEvent;
import org.reactome.web.pwp.client.common.events.ToolSelectedEvent;
import org.reactome.web.pwp.client.common.module.AbstractPresenter;

import static org.reactome.web.pwp.client.tools.launcher.ToolLauncher.ToolStatus.ACTIVE;
import static org.reactome.web.pwp.client.tools.launcher.ToolLauncher.ToolStatus.ERROR;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ToolLauncherPresenter extends AbstractPresenter implements ToolLauncher.Presenter {

    @SuppressWarnings("FieldCanBeLocal")
    private ToolLauncher.Display display;

    public ToolLauncherPresenter(EventBus eventBus, ToolLauncher.Display display) {
        super(eventBus);
        this.display = display;
        this.display.setPresenter(this);

//        this.checkAnalysisStatus();
    }

    @Override
    public void onStateChanged(StateChangedEvent event) {
        //Nothing here
    }

    @Override
    public void toolSelected(PathwayPortalTool tool) {
        this.eventBus.fireEventFromSource(new ToolSelectedEvent(tool), this);
    }

    private void checkAnalysisStatus() {
        AnalysisClient.getDatabaseInformation(new AnalysisHandler.DatabaseInformation() {
            @Override
            public void onDBInfoLoaded(DBInfo dbInfo) {
                display.setStatus(ACTIVE);
            }

            @Override
            public void onDBInfoError(AnalysisError error) {
                display.setStatus(ERROR);
            }

            @Override
            public void onAnalysisServerException(String message) {
                display.setStatus(ERROR);
            }
        });
    }
}
