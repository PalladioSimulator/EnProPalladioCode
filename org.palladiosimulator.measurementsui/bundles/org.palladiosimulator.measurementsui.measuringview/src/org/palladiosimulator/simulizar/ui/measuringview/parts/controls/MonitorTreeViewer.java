package org.palladiosimulator.simulizar.ui.measuringview.parts.controls;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.parsley.composite.TreeFormComposite;
import org.eclipse.emf.parsley.composite.TreeFormFactory;
import org.eclipse.emf.parsley.edit.ui.dnd.ViewerDragAndDropHelper;
import org.eclipse.emf.parsley.menus.ViewerContextMenuHelper;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepositoryFactory;

import dataManagement.DataGathering;
import init.DataApplication;

import com.google.inject.Injector;

import mpview.MpviewInjectorProvider;

/**
 * 
 * @author David Schütz
 *
 */
public class MonitorTreeViewer extends MpTreeViewer {

	TreeFormComposite treeFormComposite;
	Injector injector;
	EObject monitorRepository;
	
	/**
	 * 
	 * @param parent
	 * @param dirty
	 * @param commandService
	 */
	public MonitorTreeViewer(Composite parent, MDirtyable dirty,ECommandService commandService, DataApplication application) {
		super(parent, dirty, commandService, application);
	}

	@Override
	protected void initParsley(Composite parent) {
		// Siehe Eclipse 4.x in der Parsley Doku. Je nach Darstellungsart(Tree, Form,
		// Table, TreeForm,...) des Parsleyprojektes muss der Code hier
		// leicht modifiziert werden.

		// Guice injector
		injector = MpviewInjectorProvider.getInjector();

		// Get the Path of MonitorRepository file of first project in Workspace that
		// also has an .aird file
		
		EditingDomain editingDomain = getEditingDomain(injector);
		
		this.monitorRepository = dataApplication.getModelAccessor().getMonitorRepository().get(0);
		resource = getResource(monitorRepository, editingDomain, injector);

		TreeFormFactory treeFormFactory = injector.getInstance(TreeFormFactory.class);
		// create the tree-form composite
		treeFormComposite = treeFormFactory.createTreeFormComposite(parent, SWT.BORDER);

		// Guice injected viewer context menu helper
		ViewerContextMenuHelper contextMenuHelper = injector.getInstance(ViewerContextMenuHelper.class);
		// Guice injected viewer drag and drop helper
		ViewerDragAndDropHelper dragAndDropHelper = injector.getInstance(ViewerDragAndDropHelper.class);

		// set context menu and drag and drop
		contextMenuHelper.addViewerContextMenu(treeFormComposite.getViewer(), editingDomain);

		// Leider ist das Drag and Drop in Parsley f�r unser Projekt nicht so
		// geeignet, da es lediglich auf EMF.Edit basiert.
		// Wahrscheinlich m�ssen wir eine eigene DragandDrop Funktion programmieren.
		// Oder besser aber auf unserem eigenen ECoreModell arbeiten, wo das dann alles
		// funktioniert :)
		dragAndDropHelper.addDragAndDrop(treeFormComposite.getViewer(), editingDomain);

		// update the composite
		treeFormComposite.update(resource);

		this.treeViewer = (TreeViewer) treeFormComposite.getViewer();
		

	}

	@Override
	public void updateTree() {
		
		this.monitorRepository = dataApplication.getModelAccessor().getMonitorRepository().get(0);
		resource = getResource(this.monitorRepository, getEditingDomain(injector), injector);
		treeFormComposite.update(resource);
	}

	public Resource getResource() {
		return resource;
	}

	@Override
	public void dispose() {
		this.treeFormComposite.dispose();
	}

}