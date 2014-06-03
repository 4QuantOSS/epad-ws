package edu.stanford.epad.epadws.dcm4chee;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.epad.dtos.internal.DCM4CHEEStudySearchType;
import edu.stanford.epad.epadws.queries.Dcm4CheeQueries;

/**
 * Defines all operations on the dcm4chee database used by ePAD.
 * 
 * @author martin
 * @see Dcm4CheeQueries
 */
public interface Dcm4CheeDatabaseOperations
{
	/**
	 * Get all dcm4chee studies that have finished processing.
	 */
	Set<String> getAllReadyDcm4CheeSeriesUIDs();

	/**
	 * Returns a map describing a dcm4chee series with the following keys: pk, study_fk, mpps_fk, inst_code_fk,
	 * series_iuid, series_no, modality, body_part, laterality, series_desc, institution, station_name, department,
	 * perf_physician, perf_phys_fn_sx, perf_phys_gn_sx perf_phys_i_name, perf_phys_p_name, pps_start, series_custom1,
	 * series_custom2, series_custom3, num_instances, src_aet, ext_retr_aet, retrieve_aets, fileset_iuid, fileset_id,
	 * availability, series_status, created_time, updated_time, series_attrs
	 */
	Map<String, String> getSeriesData(String seriesUID);

	/**
	 * typeValue one of: patientName, patientId, studyDate, accessionNum, examType
	 * 
	 * @see DCM4CHEEStudySearchType
	 */
	List<Map<String, String>> studySearch(DCM4CHEEStudySearchType searchType, String typeValue);

	Map<String, String> studySearch(String studyUID);

	List<Map<String, String>> findAllDicomSeriesInStudy(String studyUID);

	Set<String> findAllSeriesUIDsInStudy(String studyUID);

	Map<String, String> getPatientForStudy(String studyUID);

	Set<String> getStudyUIDsForPatient(String patientID);

	Set<String> getImageUIDsForSeries(String seriesID);

	int getNumberOfStudiesForPatient(String patientID);

	int getNumberOfStudiesForPatients(Set<String> patientIDs);

	Map<String, String> getParentStudyForSeries(String seriesIUID);

	String getStudyUIDForSeries(String seriesUID);

	String getSeriesUIDForImage(String imageUID);

	List<Map<String, String>> getDicomImageFileDescriptionsForSeries(String seriesUID);

	// Returns a list of image descriptions; each description is a map containing the keys to the instance table in the
	// pscsdb MySql database. The keys to get the image ID and instance number are sop_iuid and inst_no, respectively.
	List<Map<String, String>> getImageDescriptions(String seriesUID);

	Map<String, String> getImageDescription(String seriesUID, String imageID);

	int getPrimaryKeyForInstanceUID(String imageUID);
}
