package edu.stanford.isis.epadws.processing.pipeline.task;

import edu.stanford.epad.dtos.DCM4CHEESeries;
import edu.stanford.epad.dtos.DCM4CHEESeriesList;
import edu.stanford.isis.epad.common.util.EPADLogger;
import edu.stanford.isis.epadws.dcm4chee.Dcm4CheeOperations;
import edu.stanford.isis.epadws.epaddb.EpadDatabase;
import edu.stanford.isis.epadws.epaddb.EpadDatabaseOperations;
import edu.stanford.isis.epadws.epaddb.FileOperations;
import edu.stanford.isis.epadws.queries.Dcm4CheeQueries;

/**
 * Task to delete a DICOM study
 * 
 */
public class DicomStudyDeleteTask implements Runnable
{
	private static EPADLogger logger = EPADLogger.getInstance();
	private final String studyUID;

	public DicomStudyDeleteTask(String studyUID)
	{
		this.studyUID = studyUID;
	}

	@Override
	public void run()
	{
		EpadDatabaseOperations epadDatabaseOperations = EpadDatabase.getInstance().getEPADDatabaseOperations();

		try {
			DCM4CHEESeriesList dcm4CheeSeriesList = Dcm4CheeQueries.getSeriesInStudy(studyUID);
			logger.info("Found " + dcm4CheeSeriesList.ResultSet.totalRecords + " series in study " + studyUID);

			Dcm4CheeOperations.deleteStudy(studyUID); // Must run after finding series in DCM4CHEE

			// Should not delete until after deleting study in DCM4CHEE or PNG pipeline will activate.
			for (DCM4CHEESeries series : dcm4CheeSeriesList.ResultSet.Result) {
				logger.info("SeriesID to delete in ePAD database: " + series.seriesUID);
				epadDatabaseOperations.deleteDicomSeries(series.seriesUID);
			}
			epadDatabaseOperations.deleteDicomStudy(studyUID);
			FileOperations.deletePNGsforDicomStudy(studyUID);
		} catch (Exception e) {
			logger.warning("run had: " + e.getMessage(), e);
		}
	}
}
