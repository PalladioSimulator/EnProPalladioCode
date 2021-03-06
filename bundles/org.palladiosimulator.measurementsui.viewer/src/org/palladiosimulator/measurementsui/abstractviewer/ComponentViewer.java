package org.palladiosimulator.measurementsui.abstractviewer;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.parsley.edit.ui.dnd.ViewerDragAndDropHelper;
import org.eclipse.emf.parsley.menus.ViewerContextMenuHelper;
import org.eclipse.emf.parsley.resource.ResourceLoader;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;

import com.google.inject.Injector;

/**
 * Abstract component class to connect a parsley view to an eclipse 4 plugin
 * 
 * @author David Schuetz
 *
 */
public abstract class ComponentViewer {
    protected Injector injector;
    protected Resource resource;
    protected EditingDomain editingDomain;
    protected EObject modelRepository;

    /**
     * @param parent
     *            container where the tree viewer is placed in
     * @param modelRepository
     *            EObject which is shown in the view
     * 
     */
    public ComponentViewer(Composite parent, EObject modelRepository) {
        this.modelRepository = modelRepository;
        initInjector();
        initEditingDomain();
        initParsley(parent);
        initContextMenu();
        initDragAndDrop();
    }

    /**
     * Initalizes the google guice injector attribute with the injector of the respective parsley
     * view
     */
    protected abstract void initInjector();

    /**
     * 
     * @return the repository of the current view. For Example the monitorrepository
     */
    protected Optional<EObject> getModelRepository() {
        if (modelRepository != null) {
            return Optional.of(modelRepository);
        }
        return Optional.empty();
    }

    public void setModelRepository(EObject modelRepository) {
        this.modelRepository = modelRepository;
    }

    /**
     * Initialize the connection between the e4 plugin and the Parsley TreeView
     * 
     * @param parent
     *            composite container
     */
    protected abstract void initParsley(Composite parent);

    /**
     * Updates the underlying resources of the tree and redraws the component
     * 
     */
    public abstract void update();

    /**
     * Returns the parsley EditingDomain
     */
    protected void initEditingDomain() {
        getModelRepository().ifPresent(modelRepository -> 
        this.editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(modelRepository));
    }

    /**
     * 
     * @return the current viewer of the shown composite
     */
    public abstract StructuredViewer getViewer();

    /**
     * @param model
     *            EMF Model of the shown data
     * @return the resource using the resource set of the editing domain
     */
    protected Resource updateResource(EObject model) {
        ResourceLoader resourceLoader = injector.getInstance(ResourceLoader.class);
        resource = resourceLoader.getResource(editingDomain, model.eResource().getURI()).getResource();
        return resource;
    }

    /**
     * Initialize the parsley drag and drop function
     */
    protected void initDragAndDrop() {
        ViewerDragAndDropHelper dragAndDropHelper = injector.getInstance(ViewerDragAndDropHelper.class);
        dragAndDropHelper.addDragAndDrop(getViewer(), editingDomain);
    }

    /**
     * Initialize the parsley context menu
     */
    protected void initContextMenu() {
        ViewerContextMenuHelper contextMenuHelper = injector.getInstance(ViewerContextMenuHelper.class);
        contextMenuHelper.addViewerContextMenu(getViewer(), editingDomain);
    }
}