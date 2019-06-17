package org.reactome.web.pwp.client.tools.analysis.gsa.steps;


import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.reactome.web.pwp.client.details.common.widgets.button.IconButton;
import org.reactome.web.pwp.client.tools.analysis.gsa.client.model.raw.Method;
import org.reactome.web.pwp.client.tools.analysis.gsa.common.GSAWizardContext;
import org.reactome.web.pwp.client.tools.analysis.gsa.common.GSAWizardEventBus;
import org.reactome.web.pwp.client.tools.analysis.gsa.common.widgets.MethodItem;
import org.reactome.web.pwp.client.tools.analysis.gsa.events.StepSelectedEvent;
import org.reactome.web.pwp.client.tools.analysis.gsa.style.GSAStyleFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SelectMethod extends AbstractGSAStep implements MethodItem.Handler {
    private List<Method> availableMethods = new ArrayList<>();
    private MethodItem selectedMethodItem;

    private FlowPanel methodsPanel;
    private IconButton nextBtn;

    public SelectMethod(GSAWizardEventBus eventBus, GSAWizardContext context) {
        super(eventBus, context);
        init();
    }

    public void setAvailableMethods(List<Method> methods) {
        this.availableMethods = methods;
        updateMethodsPanel();
    }

    @Override
    public void onCheckedChanged(MethodItem source, boolean isChecked) {
        selectedMethodItem = isChecked ? source : null;
        methodsPanel.forEach(widget -> ((MethodItem) widget).setChecked(widget.equals(source) && isChecked) );
        nextBtn.setEnabled(selectedMethodItem != null);
    }

    @Override
    public void onOptionsExpanded(MethodItem source) {
        //Collapse all of them first
        methodsPanel.forEach(widget -> ((MethodItem) widget).collapse() );
    }

    private void init() {
        FlowPanel container = new FlowPanel();
        container.setStyleName(GSAStyleFactory.getStyle().container());

        SimplePanel title = new SimplePanel();
        title.setStyleName(GSAStyleFactory.getStyle().title());
        title.getElement().setInnerHTML("Step 1: Select one of the available analysis methods");
        container.add(title);

        methodsPanel = new FlowPanel();
        methodsPanel.setStyleName(GSAStyleFactory.getStyle().methodsPanel());
        container.add(methodsPanel);

        nextBtn = new IconButton(
                "Continue",
                GSAStyleFactory.RESOURCES.nextIcon(),
                GSAStyleFactory.getStyle().navigationBtn(),
                "Define your datasets",
                event -> {
                    if(selectedMethodItem.validateAllParameters()) {
                        wizardContext.setMethod(selectedMethodItem.getMethod().getName());
                        wizardContext.setParameters(selectedMethodItem.getParameterValues());
                        wizardEventBus.fireEventFromSource(new StepSelectedEvent(GSAStep.DATASETS), this);
                    }
                });
        nextBtn.setEnabled(selectedMethodItem != null);

        addRightButton(nextBtn);

        add(new ScrollPanel(container));

    }

    private void updateMethodsPanel() {
        methodsPanel.clear();
        for (Method method : availableMethods) {
            methodsPanel.add(new MethodItem(method, this));
        }
    }


}
