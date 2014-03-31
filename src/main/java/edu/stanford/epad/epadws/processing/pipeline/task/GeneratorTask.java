package edu.stanford.epad.epadws.processing.pipeline.task;

import java.io.File;

/**
 * Task generator for both DICOM images and DICOM Segmentation Objects and PNG grid generation..
 * 
 * @author alansnyder
 */
public interface GeneratorTask extends Runnable
{
	File getInputFile();

	String getTagFilePath();

	String getTaskType();
}