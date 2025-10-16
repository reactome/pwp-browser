package org.reactome.web.pwp.client.toppanel.beta;

import org.reactome.web.pwp.client.common.module.BrowserModule;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface Beta {

    interface Presenter extends BrowserModule.Presenter {
        void openBeta();
    }

    interface Display extends BrowserModule.Display {
        void setPresenter(Presenter presenter);
        void triggerAnimation();
    }
}
