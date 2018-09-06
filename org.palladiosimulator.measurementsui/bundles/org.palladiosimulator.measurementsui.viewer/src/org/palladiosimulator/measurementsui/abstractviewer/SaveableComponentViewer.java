package org.palladiosimulator.measurementsui.abstractviewer;

import java.io.IOException;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.palladiosimulator.measurementsui.dataprovider.DataApplication;

/**
 * A common saveable view based on a parsley view.
 * 
 * @author David Schuetz
 */
public abstract class SaveableComponentViewer extends ComponentViewer {

	protected MDirtyable dirty;
	protected ECommandService commandService;

	/**
	 * 
	 * @param parent         container where the view is embedded
	 * @param dirty          describes whether the view was edited
	 * @param commandService eclipse command
	 * @param application    Connection to the data binding. This is needed in order
	 *                       to get the repository of the current project.
	 * @param enableDragDrop  Specifies whether the parsley drag and drop function
	 * 						  should be used.
	 */
	protected SaveableComponentViewer(Composite parent, MDirtyable dirty, ECommandService commandService,
			DataApplication application, boolean enableDragDrop) {
		super(parent, application,enableDragDrop);
		this.dirty = dirty;
		this.commandService = commandService;
	}

	/**
	 * Connects the current selected item in the view with the eclipse
	 * selectionservice
	 * 
	 * @param selectionService of the eclipse project
	 */
	public abstract void addSelectionListener(ESelectionService selectionService);

	@Override
	protected Resource updateResource(EObject model) {
		resource = super.updateResource(model);
		initResourceChangedListener(editingDomain);
		return resource;
	}

	/**
	 * Initializes a Listener to the editing domain, which activates the dirty state
	 * if something is changed.
	 * 
	 * @param editingDomain where the listener is added to
	 */
	private void initResourceChangedListener(EditingDomain editingDomain) {
		editingDomain.getCommandStack().addCommandStackListener(e -> {
			if (dirty != null) {
				dirty.setDirty(true);
				commandService.getCommand("org.eclipse.ui.file.save").isEnabled();
			}
		});
	}

	/**
	 * Saves the current state of the view
	 * 
	 * @param dirty	describes whether the view was edited
	 * @throws IOException if the save operation fails
	 */
	public void save(MDirtyable dirty) throws IOException {
		resource.save(null);
		if (dirty != null) {
			dirty.setDirty(false);
			commandService.getCommand("org.eclipse.ui.file.save").isEnabled();
		}
	}
}