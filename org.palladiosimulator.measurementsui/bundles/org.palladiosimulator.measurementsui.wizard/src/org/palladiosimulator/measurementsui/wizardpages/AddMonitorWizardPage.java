package org.palladiosimulator.measurementsui.wizardpages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.palladiosimulator.measurementsui.wizard.viewer.MonitorFormViewer;
import org.palladiosimulator.measurementsui.wizardmodel.pages.MonitorCreationWizardModel;
import org.palladiosimulator.monitorrepository.Monitor;

/**
 * This class handles the first wizard page for creating a new monitor.
 * @author Birasanth Pushpanathan
 * @author David Schuetz Added WizardModel integration
 * @author Lasse Merz Added WizardModel integration
 *
 */
public class AddMonitorWizardPage extends WizardPage {

    /**
     * the newly created monitor object
     */
	private MonitorCreationWizardModel model;

    /**
     * Constructor, sets basic attributes for the wizard page like title, description etc.
     * @param model the model object which manages the internal model for this wizard page
     */
    public AddMonitorWizardPage(MonitorCreationWizardModel model) {
        super("First Page");
        this.model = model;
        setTitle(model.getTitleText());
        setDescription(model.getInfoText());
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        
        parent.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
        
        FillLayout layout = new FillLayout();
        container.setLayout(layout);

        setControl(container);
        setPageComplete(true);

        createMonitorFormViewer(container, model.getMonitor(), this);
    }
    
    /**
     * Creates a form view where the user can edit different properties for the newly created monitor object.
     * @param parent the parent composite that contains the form view
     * @param newMonitor the newly created monitor object
     * @param wizardPage this wizard page
     * @return the form view 
     */
    private MonitorFormViewer createMonitorFormViewer(Composite parent, Monitor newMonitor, AddMonitorWizardPage wizardPage) {
        return new MonitorFormViewer(parent, newMonitor, wizardPage);
    }

    @Override
    public void performHelp() {
        Shell shell = new Shell(getShell());
        shell.setText("My Custom Help !!");
        shell.setLayout(new GridLayout());
        shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Browser browser = new Browser(shell, SWT.NONE);
        browser.setUrl("http://stackoverflow.com/questions/7322489/cant-put-content-behind-swt-wizard-help-button");
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        shell.open();
    }

}
