package org.palladiosimulator.measurementsui.dataprovider;

import java.util.Collection;
import java.util.Map;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.measurementsui.datamanipulation.ResourceEditorImpl;
import org.palladiosimulator.measurementsui.extensionpoint.evaluation.EvaluateExtensions;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepositoryFactory;
import org.palladiosimulator.monitorrepository.MonitorRepositoryPackage;
import org.palladiosimulator.pcmmeasuringpoint.ActiveResourceMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.AssemblyOperationMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.AssemblyPassiveResourceMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.EntryLevelSystemCallMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.ExternalCallActionMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.LinkingResourceMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.ResourceContainerMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.ResourceEnvironmentMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.SubSystemOperationMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.SystemOperationMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.UsageScenarioMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.util.PcmmeasuringpointSwitch;

/**
 * This class creates and manages the Monitors used for the 3rd Wizard Page
 * 
 * @author Florian Nieuwenhuizen
 *
 */
public class UnselectedMetricSpecificationsProvider {

    private ResourceEditorImpl editor = ResourceEditorImpl.getInstance();

    /**
     * Creates a Monitor with all of the Metric Description <-> Measurement Specification pairs that
     * do not already exist in the passedMonitor needed for the left half of the 3rd Wizard Page
     * 
     * @param passedMonitor
     *            The Monitor that gets passed from the previous Wizard Page
     */
    public void createMonitorWithMissingMetricDescriptions(Monitor passedMonitor, Monitor unusedMonitor,
            boolean expertMode) {
        MonitorRepositoryFactory monFactory = MonitorRepositoryPackage.eINSTANCE.getMonitorRepositoryFactory();

        // Only way to get all Metric Descriptions from scratch
        MetricSetDescription dummyMetricDesc = MetricDescriptionConstants.COST_OVER_TIME;
        EList<MetricDescription> allMetricDescriptions = dummyMetricDesc.getRepository().getMetricDescriptions();

        EList<MetricDescription> validMetricDescriptionList = new BasicEList<>();
        if (passedMonitor != null) {
            if (getAllValidMetricDescriptionsForMeasuringPoint(passedMonitor) != null) {
                validMetricDescriptionList
                        .addAll(getAllValidMetricDescriptionsForMeasuringPoint(passedMonitor).keySet());
            }

            EList<MeasurementSpecification> mSpecsOfPassedMonitor = passedMonitor.getMeasurementSpecifications();

            if (!mSpecsOfPassedMonitor.isEmpty()) {
                mSpecsOfPassedMonitor = passedMonitor.getMeasurementSpecifications();
                EList<MetricDescription> metricDescriptionsInPassedMonitor = new BasicEList<>();

                for (MeasurementSpecification aMSpec : mSpecsOfPassedMonitor) {
                    metricDescriptionsInPassedMonitor.add(aMSpec.getMetricDescription());
                }
                EList<MetricDescription> nonMatchingMetricDesciptions = new BasicEList<>();

                if (expertMode) {
                    findNonMatchingMetricDescriptions(metricDescriptionsInPassedMonitor, allMetricDescriptions,
                            nonMatchingMetricDesciptions);
                } else {
                    findNonMatchingMetricDescriptions(metricDescriptionsInPassedMonitor, validMetricDescriptionList,
                            nonMatchingMetricDesciptions);
                }

                createMonitorWithMissingDescriptions(monFactory, nonMatchingMetricDesciptions, unusedMonitor);

            } else {
                if (expertMode) {
                    createMonitorWithMissingDescriptions(monFactory, allMetricDescriptions, unusedMonitor);
                } else {
                    createMonitorWithMissingDescriptions(monFactory, validMetricDescriptionList, unusedMonitor);
                }

            }
        }

    }

    /**
     * returns the map of valid MetricDescription, boolean(suggested) for the corresponding
     * Measuring Point.
     * 
     * @param passedMP
     * @return the map Of MetricDescription, boolean(suggested) pairs.
     */
    public Map<MetricDescription, Boolean> getAllValidMetricDescriptionsForMeasuringPoint(Monitor passedMonitor) {
        if (passedMonitor.getMeasuringPoint() != null) {

            MeasuringPoint passedMP = passedMonitor.getMeasuringPoint();
            EvaluateExtensions evaluateExtensions = new EvaluateExtensions();
            evaluateExtensions.loadExtensions();

            PcmmeasuringpointSwitch<Map<MetricDescription, Boolean>> measuringPointSwitch = getPcmMeasuringPointSwitch(
                    evaluateExtensions);
            return measuringPointSwitch.doSwitch(passedMP);
        }
        return null;
    }

    /**
     * switch over the different kinds of Measuring Points.
     * 
     * @param evaluateExtensions
     * @return
     */
    private PcmmeasuringpointSwitch<Map<MetricDescription, Boolean>> getPcmMeasuringPointSwitch(
            EvaluateExtensions evaluateExtensions) {
        return new PcmmeasuringpointSwitch<Map<MetricDescription, Boolean>>() {

            @Override
            public Map<MetricDescription, Boolean> caseActiveResourceMeasuringPoint(
                    ActiveResourceMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getActiveResourceMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseAssemblyOperationMeasuringPoint(
                    AssemblyOperationMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getAssemblyOperationMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseAssemblyPassiveResourceMeasuringPoint(
                    AssemblyPassiveResourceMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getAssemblyPassiveResourceMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseEntryLevelSystemCallMeasuringPoint(
                    EntryLevelSystemCallMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getEntryLevelSystemCallMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseExternalCallActionMeasuringPoint(
                    ExternalCallActionMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getExternalCallActionMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseLinkingResourceMeasuringPoint(
                    LinkingResourceMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getLinkingResourceMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseResourceContainerMeasuringPoint(
                    ResourceContainerMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getResourceContainerMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseResourceEnvironmentMeasuringPoint(
                    ResourceEnvironmentMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getResourceEnvironmentMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseSubSystemOperationMeasuringPoint(
                    SubSystemOperationMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getSubSystemOperationMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseSystemOperationMeasuringPoint(
                    SystemOperationMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getSystemOperationMeasuringPointMetrics();
            }

            @Override
            public Map<MetricDescription, Boolean> caseUsageScenarioMeasuringPoint(UsageScenarioMeasuringPoint object) {
                return evaluateExtensions.getMeasuringPointmetricsCombinations()
                        .getUsageScenarioMeasuringPointMetrics();
            }
        };

    }

    /**
     * Creates a Monitor with all MetricDesc<->MeasurementSpec pairs for every MetricDesc from the
     * listOfMetricDescriptions
     * 
     * @param monFactory
     * @param listOfMetricDescriptions
     * @return
     */
    private Monitor createMonitorWithMissingDescriptions(MonitorRepositoryFactory monFactory,
            EList<MetricDescription> listOfMetricDescriptions, Monitor tempMon) {
        EList<MeasurementSpecification> mSpecList = new BasicEList<>();
        createMeasurementSpecificationsForEveryMetricDescription(listOfMetricDescriptions, monFactory, mSpecList);
        setMetricDescriptionForEveryMeasurementSpecification(listOfMetricDescriptions, mSpecList);
        for (MeasurementSpecification aMSpec : mSpecList) {
            // has to be adapted once the 4th screen is implemented
            aMSpec.setProcessingType(MonitorRepositoryFactory.eINSTANCE.createFeedThrough());
        }
        tempMon.eSet(tempMon.eClass().getEStructuralFeature("measurementSpecifications"), mSpecList);
        return tempMon;
    }

    /**
     * Moves the selected Measurement Specification from one Monitor to the other. Needed for
     * implementation of Left/Right Buttons in Wizard Page 3
     * 
     * @param selectedMetricDescription
     * @param sendingMonitor
     * @param receivingMonitor
     */
    public void moveMeasurementSpecificationToMonitor(MeasurementSpecification selectedMeasurementSpecification,
            Monitor receivingMonitor, boolean isInEditMode) {
        if (isInEditMode) {
            editor.addMeasurementSpecificationToMonitor(receivingMonitor, selectedMeasurementSpecification);
        } else {
            receivingMonitor.getMeasurementSpecifications().add(selectedMeasurementSpecification);
        }
    }

    /**
     * Removes the measurement specification from the receiving monitor. If in edit mode the
     * resource needs to be deleted through emf command to avoid write transaction errors
     * 
     * @param selectedMeasurementSpecification
     * @param receivingMonitor
     * @param isInEditMode
     */
    public void removeMeasurementSpecificationFromMonitor(MeasurementSpecification selectedMeasurementSpecification,
            Monitor receivingMonitor, boolean isInEditMode) {
        if (isInEditMode) {

            editor.deleteResource(selectedMeasurementSpecification);
            receivingMonitor.getMeasurementSpecifications().add(selectedMeasurementSpecification);
        } else {
            receivingMonitor.getMeasurementSpecifications().add(selectedMeasurementSpecification);
        }
    }

    /**
     * Moves all Measurement Specifications from one Monitor to another. Needed for the double Arrow
     * (move All) in the 3rd Wizard Page
     * 
     * @param sendingMonitor
     * @param receivingMonitor
     */
    public void moveAllMeasurementSpecificationsToMonitor(Monitor sendingMonitor, Monitor receivingMonitor,
            boolean isInEditMode) {
        if (isInEditMode) {
            editor.addMeasurementSpecificationsToMonitor(receivingMonitor,
                    sendingMonitor.getMeasurementSpecifications());
        } else {
            receivingMonitor.getMeasurementSpecifications().addAll(sendingMonitor.getMeasurementSpecifications());
        }

    }

    /**
     * Moves the Metric Descriptions that are flagged as suggested to the receiving Monitor. Checks
     * if receiving Monitor does not already have the Metric Description.
     * 
     * @param sendingMonitor
     * @param receivingMonitor
     * @param isInEditMode
     */
    public void moveSuggestedMeasurementSpecificationsToMonitor(Monitor sendingMonitor, Monitor receivingMonitor,
            boolean isInEditMode) {
        EList<MeasurementSpecification> allMSpecs = sendingMonitor.getMeasurementSpecifications();
        EList<MeasurementSpecification> moveList = new BasicEList<>();
        Map<MetricDescription, Boolean> validMap = getAllValidMetricDescriptionsForMeasuringPoint(receivingMonitor);
        EList<MeasurementSpecification> mSpecsInReceivingMonitor = receivingMonitor.getMeasurementSpecifications();
        for (Map.Entry<MetricDescription, Boolean> entry : validMap.entrySet()) {
            if (entry.getValue()) {
                for (MeasurementSpecification aMSpec : allMSpecs) {
                    if (aMSpec.getMetricDescription().getName().equals(entry.getKey().getName())
                            && !mSpecsInReceivingMonitor.contains(aMSpec)) {
                        moveList.add(aMSpec);
                    }
                }

            }
        }
        if (isInEditMode) {
            editor.addMeasurementSpecificationsToMonitor(receivingMonitor, moveList);
        } else {
            receivingMonitor.getMeasurementSpecifications().addAll(moveList);
        }

    }

    /**
     * Removes all MeasurementSpecifications from the Wizard Monitor and adds them back to the temp
     * Monitor. EditMode requires working with a deep copy to first move to temp monitor and then
     * delete from wizard Monitor.
     * 
     * @param sendingMonitor
     * @param receivingMonitor
     * @param isInEditMode
     */
    public void removeAllMeasurementSpecificationsFromMonitor(Monitor sendingMonitor, Monitor receivingMonitor,
            boolean isInEditMode) {
        if (isInEditMode) {
            Collection<MeasurementSpecification> copy = EcoreUtil
                    .copyAll(sendingMonitor.getMeasurementSpecifications());
            receivingMonitor.getMeasurementSpecifications().addAll(copy);
            editor.deleteMultipleResources(sendingMonitor.getMeasurementSpecifications());
        } else {
            receivingMonitor.getMeasurementSpecifications().addAll(sendingMonitor.getMeasurementSpecifications());
        }
    }

    /**
     * Sets a Metric Description for every Measurement Specification
     * 
     * @param nonMatchingMetricDesciptions
     * @param mSpecList
     */
    public void setMetricDescriptionForEveryMeasurementSpecification(
            EList<MetricDescription> nonMatchingMetricDesciptions, EList<MeasurementSpecification> mSpecList) {
        for (int i = 0; i < mSpecList.size(); i++) {
            mSpecList.get(i).setMetricDescription(nonMatchingMetricDesciptions.get(i));
        }
    }

    /**
     * Create a MSpec for every Metric Description
     * 
     * @param nonMatchingMetricDesciptions
     * @param monFactory
     * @param mSpecList
     */
    public void createMeasurementSpecificationsForEveryMetricDescription(
            EList<MetricDescription> nonMatchingMetricDesciptions, MonitorRepositoryFactory monFactory,
            EList<MeasurementSpecification> mSpecList) {
        for (int i = 0; i < nonMatchingMetricDesciptions.size(); i++) {
            mSpecList.add(monFactory.createMeasurementSpecification());
        }
    }

    /**
     * Finds the Metric Descriptions that are not in the passedMonitor.
     * 
     * @param metricDescInPassedMonitor
     * @param allMetricDescriptions
     * @param nonMatchingMetricDesciptions
     * @return
     */
    private EList<MetricDescription> findNonMatchingMetricDescriptions(
            EList<MetricDescription> metricDescInPassedMonitor, EList<MetricDescription> allMetricDescriptions,
            EList<MetricDescription> nonMatchingMetricDesciptions) {
        for (MetricDescription allMetricDesc : allMetricDescriptions) {
            boolean nonMatching = true;
            for (MetricDescription aMetricDesc : metricDescInPassedMonitor) {
                if (allMetricDesc.getName().equals(aMetricDesc.getName())) {
                    nonMatching = false;
                    break;
                }
            }
            if (nonMatching) {
                nonMatchingMetricDesciptions.add(allMetricDesc);
            }

        }
        return nonMatchingMetricDesciptions;
    }

    /**
     * Returns the textualDescription of a Metric Description.
     * 
     * @param aMetricDescription
     * @return
     */
    public String provideTextualDescriptionForMetricDescription(MetricDescription aMetricDescription) {
        return aMetricDescription.getTextualDescription();
    }
}
