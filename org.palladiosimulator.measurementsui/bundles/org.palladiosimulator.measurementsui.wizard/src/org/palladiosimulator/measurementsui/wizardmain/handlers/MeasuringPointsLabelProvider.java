package org.palladiosimulator.measurementsui.wizardmain.handlers;

import java.util.LinkedList;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.composition.provider.AssemblyContextItemProvider;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.provider.PcmItemProviderAdapterFactory;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.provider.LinkingResourceItemProvider;
import org.palladiosimulator.pcm.resourceenvironment.provider.ResourceContainerItemProvider;
import org.palladiosimulator.pcm.resourceenvironment.provider.ResourceEnvironmentItemProvider;
import org.palladiosimulator.pcm.resourcetype.provider.ProcessingResourceTypeItemProvider;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.provider.ExternalCallActionItemProvider;
import org.palladiosimulator.pcm.subsystem.SubSystem;
import org.palladiosimulator.pcm.subsystem.provider.SubSystemItemProvider;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.pcm.system.provider.SystemItemProvider;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.provider.EntryLevelSystemCallItemProvider;
import org.palladiosimulator.pcm.usagemodel.provider.UsageModelItemProvider;

/**
 * 
 * @author Domas Mikalkinas
 *
 */
public class MeasuringPointsLabelProvider implements ILabelProvider {
    EMFPlugin plug = new EcoreEditPlugin();

    @Override
    public void addListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Image getImage(Object element) {
        PcmItemProviderAdapterFactory factory = new PcmItemProviderAdapterFactory();

        if (element instanceof LinkedList) {
            return null;
        } else {
            EObject object = (EObject) element;
            if (object instanceof UsageScenario) {
                UsageModelItemProvider mod = new UsageModelItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            } else if (object instanceof ResourceEnvironment) {
                ResourceEnvironmentItemProvider mod = new ResourceEnvironmentItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));

            } else if (object instanceof System) {
                SystemItemProvider mod = new SystemItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            } else if (object instanceof AssemblyContext) {
                AssemblyContextItemProvider mod = new AssemblyContextItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            } else if (object instanceof ResourceContainer) {
                ResourceContainerItemProvider mod = new ResourceContainerItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            } else if (object instanceof ProcessingResourceSpecification) {
                ProcessingResourceTypeItemProvider mod = new ProcessingResourceTypeItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            } else if (object instanceof LinkingResource) {
                LinkingResourceItemProvider mod = new LinkingResourceItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            } else if (object instanceof ExternalCallAction) {
                ExternalCallActionItemProvider mod = new ExternalCallActionItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            } else if (object instanceof EntryLevelSystemCall) {
                EntryLevelSystemCallItemProvider mod = new EntryLevelSystemCallItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            } else if (object instanceof SubSystem) {
                SubSystemItemProvider mod = new SubSystemItemProvider(factory);
                return ExtendedImageRegistry.getInstance().getImage(mod.getImage(object));
            }

        }

        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof LinkedList) {
            if (!((LinkedList<?>) element).isEmpty()) {
                return (((LinkedList<?>) element).get(0).getClass().getSimpleName().replaceAll("Impl", "")
                        .replaceAll("([A-Z])", " $1"));
            } else {
                return null;
            }

        } else {
            if (element instanceof ProcessingResourceSpecification) {
                return ((ProcessingResourceSpecification) element).getActiveResourceType_ActiveResourceSpecification()
                        .getEntityName() + " located in "+ ((ProcessingResourceSpecification) element).getResourceContainer_ProcessingResourceSpecification().getEntityName();

            } else if (element instanceof AssemblyContext){
                return ((AssemblyContext) element).getEntityName() +" located in "+((AssemblyContext) element).getParentStructure__AssemblyContext().getEntityName();
                
            }else if (element instanceof ResourceContainer){
                return ((ResourceContainer) element).getEntityName() +" located in "+((ResourceContainer) element).getResourceEnvironment_ResourceContainer().getEntityName();
                
          }else if (element instanceof LinkingResource){
              return ((LinkingResource) element).getEntityName() +" located in "+((LinkingResource) element).getResourceEnvironment_LinkingResource().getEntityName();
              
        }else if (element instanceof ExternalCallAction){
            NamedElement callyMcCallface= (NamedElement) ((ExternalCallAction) element).getResourceDemandingBehaviour_AbstractAction().eContainer();
            ResourceDemandingSEFF callysFriend= (ResourceDemandingSEFF) ((ExternalCallAction) element).eContainer();
            return ((ExternalCallAction) element).getEntityName() +" from the "+callysFriend.toString().replace("[TRANSIENT]","") + " located in "+ callyMcCallface.getEntityName();
            
      }else if (element instanceof EntryLevelSystemCall){
          NamedElement callyMcCallface= (NamedElement) ((EntryLevelSystemCall) element).getScenarioBehaviour_AbstractUserAction().eContainer();
          ScenarioBehaviour callysFriend= (ScenarioBehaviour) ((EntryLevelSystemCall) element).getScenarioBehaviour_AbstractUserAction();
          return ((EntryLevelSystemCall) element).getEntityName() +" from the "+callysFriend.getEntityName() + " located in "+ callyMcCallface.getEntityName();
          
    } else if (element instanceof UsageScenario) {
        return ((UsageScenario) element).getEntityName() + " located in "+ ((UsageScenario) element).getUsageModel_UsageScenario().toString().replace("[TRANSIENT]","");

    }
            
            
            return ((NamedElement) element).getEntityName();
             
        }

    }

}
