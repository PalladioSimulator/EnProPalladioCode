package org.palladiosimulator.measurementsui.abstractviewer;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.parsley.viewers.ViewerFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.palladiosimulator.measurementsui.abstractviewer.listener.MpTreeDoubleClickListener;
import org.palladiosimulator.measurementsui.dataprovider.DataApplication;

/**
 * Creates a eclipse.swt TreeView based on a parsley TreeView project.
 * 
 * @author David Schuetz
 */
public abstract class MpTreeViewer extends SaveableComponentViewer {
	protected TreeViewer treeViewer;
	protected ViewerFactory treeFactory;

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
	public MpTreeViewer(Composite parent, MDirtyable dirty, ECommandService commandService,
			DataApplication application, boolean enableDragDrop) {
		super(parent, dirty, commandService, application, enableDragDrop);
	}

	/**
	 * Adds a DoubleClickMouseListener which changes Attributes if an icon in the
	 * treeview is double clicked.
	 */
	public void addMouseListener() {
		treeViewer.getTree().addMouseListener(new MpTreeDoubleClickListener(treeViewer));
	}

	/**
	 * Return the TreeViewer
	 * 
	 * @return The current TreeViewer
	 */
	@Override
	public StructuredViewer getViewer() {
		return treeViewer;
	}

	/**
	 * Adds a listener which connects the selected tree item to the
	 * ESelectionService.
	 * 
	 * @param selectionService
	 */
	@Override
	public void addSelectionListener(ESelectionService selectionService) {
		treeViewer.addSelectionChangedListener(event -> {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			selectionService.setSelection(selection.size() == 1 ? selection.getFirstElement() : selection.toArray());
		});
	}

	@Override
	protected void initParsley(Composite parent) {
		treeViewer = new TreeViewer(parent);
		treeFactory = injector.getInstance(ViewerFactory.class);
		update();
	}

	@Override
	public void update() {
		initEditingDomain();
		resource = updateResource(getModelRepository());
		treeFactory.initialize(treeViewer, resource);
		treeViewer.expandAll();
	}
}