package edu.stanford.isis.epadws.processing.pipeline.watcher;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import edu.stanford.epad.dtos.DCM4CHEESeries;
import edu.stanford.isis.epad.common.util.EPADLogger;
import edu.stanford.isis.epadws.dcm4chee.Dcm4CheeDatabase;
import edu.stanford.isis.epadws.dcm4chee.Dcm4CheeDatabaseOperations;
import edu.stanford.isis.epadws.epaddb.EpadDatabase;
import edu.stanford.isis.epadws.epaddb.EpadDatabaseOperations;
import edu.stanford.isis.epadws.processing.model.DicomSeriesProcessingDescription;
import edu.stanford.isis.epadws.processing.pipeline.threads.ShutdownSignal;
import edu.stanford.isis.epadws.queries.DefaultEpadQueries;
import edu.stanford.isis.epadws.queries.EpadQueries;

/**
 * Watch for new studies that appear with ePAD's DCM4CHEE MySQL database with the 'study-status' field set to zero,
 * which indicates that they are a new series. Add them to ePAD's series watcher queues to be subsequently processed by
 * watchers (currently {@link DICOMSeriesWatcher} and {@link XNATSeriesWatcher}).
 */
public class Dcm4CheeDatabaseWatcher implements Runnable
{
	private final EPADLogger logger = EPADLogger.getInstance();

	private final BlockingQueue<DicomSeriesProcessingDescription> dcm4CheeSeriesWatcherQueue;
	private final BlockingQueue<DicomSeriesProcessingDescription> xnatSeriesWatcherQueue;

	public Dcm4CheeDatabaseWatcher(BlockingQueue<DicomSeriesProcessingDescription> dicomSeriesWatcherQueue,
			BlockingQueue<DicomSeriesProcessingDescription> xnatSeriesWatcherQueue)
	{
		logger.info("Starting ePAD's DCM4CHEE database watcher");
		this.dcm4CheeSeriesWatcherQueue = dicomSeriesWatcherQueue;
		this.xnatSeriesWatcherQueue = xnatSeriesWatcherQueue;
	}

	@Override
	public void run()
	{
		ShutdownSignal signal = ShutdownSignal.getInstance();
		EpadDatabaseOperations epadDatabaseOperations = EpadDatabase.getInstance().getEPADDatabaseOperations();
		Dcm4CheeDatabaseOperations dcm4CheeDatabaseOperations = Dcm4CheeDatabase.getInstance()
				.getDcm4CheeDatabaseOperations();
		EpadQueries epadQueries = DefaultEpadQueries.getInstance();

		while (!signal.hasShutdown()) {
			try {
				List<DCM4CHEESeries> dcm4CheeSeriesList = epadQueries.getNewDcm4CheeSeriesWithStatus(0);

				for (DCM4CHEESeries dcm4CheeSeries : dcm4CheeSeriesList) {
					String seriesUID = dcm4CheeSeries.seriesUID;
					String studyUID = dcm4CheeDatabaseOperations.getStudyUIDForSeries(seriesUID);
					String patientName = dcm4CheeSeries.patientName;
					String patientID = dcm4CheeSeries.patientID;
					String seriesDesc = dcm4CheeSeries.seriesDescription;
					int numInstances = dcm4CheeSeries.imagesInSeries;
					DicomSeriesProcessingDescription dicomSeriesDescription = new DicomSeriesProcessingDescription(numInstances,
							seriesUID, studyUID, patientName, patientID);
					epadDatabaseOperations.updateDicomSeriesStatusCode(325, seriesUID);
					submitSeriesForPngGeneration(dicomSeriesDescription); // Submit this series to generate all the PNG files.
					submitSeriesForXNATGeneration(dicomSeriesDescription); // Submit this series to generate XNAT information.

					logger.info("New DICOM series (" + patientName + ", " + seriesDesc + ") found in DCM4CHEE with "
							+ numInstances + " image(s) and series ID " + seriesUID);
				}
				Thread.sleep(500);
			} catch (Exception e) {
				logger.warning("Dcm4CheeDatabaseWatcher error", e);
			}
		}
	}

	private void submitSeriesForPngGeneration(DicomSeriesProcessingDescription dicomSeriesDescription)
	{
		dcm4CheeSeriesWatcherQueue.offer(dicomSeriesDescription);
	}

	private void submitSeriesForXNATGeneration(DicomSeriesProcessingDescription dicomSeriesDescription)
	{
		xnatSeriesWatcherQueue.offer(dicomSeriesDescription);
	}
}
