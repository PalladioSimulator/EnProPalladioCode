package org.palladiosimulator.measurementsui.wizardpages;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.palladiosimulator.measurementsui.wizard.viewer.MeasurementSpecificationViewer;
import org.palladiosimulator.measurementsui.wizardmain.handlers.ProcessingTypeEditingSupport;
import org.palladiosimulator.measurementsui.wizardmain.handlers.ProcessingTypeProperty1EditingSupport;
import org.palladiosimulator.measurementsui.wizardmodel.pages.MetricDescriptionSelectionWizardModel;
import org.palladiosimulator.measurementsui.wizardmodel.pages.ProcessingTypeSelectionWizardModel;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.impl.FeedThroughImpl;
import org.palladiosimulator.monitorrepository.impl.FixedSizeAggregationImpl;
import org.palladiosimulator.monitorrepository.impl.TimeDrivenAggregationImpl;
import org.palladiosimulator.monitorrepository.impl.TimeDrivenImpl;
import org.palladiosimulator.monitorrepository.impl.VariableSizeAggregationImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.util.Policy;

/**
 * This class handles the GUI part of the fourth wizard page for measurement specification.
 * 
 * @author Mehmet, Ba
 *
 */
public class MeasurementSpecificationWizardPage extends WizardPage {

	/**
	 * This handles the internal model.
	 */
	private ProcessingTypeSelectionWizardModel processingTypeSelectionWizardModel;

	/**
	 * The constructor where basic properties are set, e. g. title, description etc.
	 * 
	 * @param processingTypeSelectionWizardModel This handles the internal model
	 */
	public MeasurementSpecificationWizardPage(ProcessingTypeSelectionWizardModel processingTypeSelectionWizardModel) {
		super("wizardPage");
		setTitle("Measurement Specification");
		setDescription("Specify the properties of the selected measurements.");
		
		this.processingTypeSelectionWizardModel = processingTypeSelectionWizardModel;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.FILL);
		FillLayout fillLayoutParentContainer = new FillLayout();
		container.setLayout(fillLayoutParentContainer);

		setControl(container);

		MeasurementSpecificationViewer measurementSpecificationViewer = new MeasurementSpecificationViewer(container,
				this.processingTypeSelectionWizardModel);
		TableViewer tableViewer = (TableViewer) measurementSpecificationViewer.getViewer();
		tableViewer.setLabelProvider(new ITableLabelProvider() {

			public void removeListener(ILabelProviderListener listener) {
				// not used
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				String result = "";
				MeasurementSpecification measurementSpecification = (MeasurementSpecification) element;
				if (columnIndex == 0) {
					result = measurementSpecification.getMetricDescription().getName();
				} else if (columnIndex == 1) {
					result = getProcessingTypeString(measurementSpecification.getProcessingType());
				} else if (columnIndex == 2) {
					result = String.valueOf(ProcessingTypeProperty1EditingSupport.test);
				} else if (columnIndex == 3) {

				}
				return result;
			}

			public void addListener(ILabelProviderListener listener) {
				// not used
			}

			public void dispose() {
				// not used
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
		});
		
		tableViewer.getTable().getColumn(0).setText("Metric Description");
		tableViewer.getTable().getColumn(1).setText("Processing Type");
		tableViewer.getTable().getColumn(2).setText("Property Value 1");
		tableViewer.getTable().getColumn(3).setText("Property Value 2");
		
		TableViewerColumn[] tableViewerColumns = getTableViewerColumns(tableViewer);
		tableViewerColumns[1].setEditingSupport(new ProcessingTypeEditingSupport(tableViewerColumns[1].getViewer(), tableViewer, 
		        this.processingTypeSelectionWizardModel));
		tableViewerColumns[2].setEditingSupport(new ProcessingTypeProperty1EditingSupport(tableViewerColumns[2].getViewer(), tableViewer));
	}

	/**
	 * Returns the TableViewerColumn objects as an array for a given TableViewer
	 * object.
	 * 
	 * @param tableViewer
	 *            the given TableViewer object
	 * @return the TableViewerColumn objects as an array for a given TableViewer
	 *         object
	 */
	private TableViewerColumn[] getTableViewerColumns(TableViewer tableViewer) {
		TableColumn[] columns = tableViewer.getTable().getColumns();
		TableViewerColumn[] viewerColumns = new TableViewerColumn[columns.length];
		for (int i = 0; i < columns.length; i++) {
			TableColumn tableColumn = columns[i];
			viewerColumns[i] = (TableViewerColumn) tableColumn.getData(Policy.JFACE + ".columnViewer");
		}
		return viewerColumns;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	//TODO: move this method to an appropriate model class
	/**
     * Returns the correct name of a given ProcessingType.
     * @param aProcessingType the given ProcessingType
     * @return the correct name of a given ProcessingType
     */
    public static String getProcessingTypeString(EObject aProcessingType) {
        String result;
        if (aProcessingType instanceof FeedThroughImpl) {
            result = "FeedThrough";
            
        } else if (aProcessingType instanceof FixedSizeAggregationImpl) {
            result = "FixedSizeAggregation";

        } else if (aProcessingType instanceof TimeDrivenImpl) {
            if (aProcessingType instanceof TimeDrivenAggregationImpl) {
                result = "TimeDrivenAggregation";

            } else {
                result = "TimeDriven";
            }

        } else if (aProcessingType instanceof VariableSizeAggregationImpl) {
            result = "VariableSizeAggregation";
        } else {
            throw new IllegalArgumentException();
        }
        return result;
    }
}