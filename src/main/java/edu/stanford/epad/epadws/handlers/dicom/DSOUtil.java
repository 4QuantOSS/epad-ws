package edu.stanford.epad.epadws.handlers.dicom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;

import edu.stanford.epad.common.dicom.DCM4CHEEUtil;
import edu.stanford.epad.common.dicom.DicomSegmentationObject;
import edu.stanford.epad.common.pixelmed.PixelMedUtils;
import edu.stanford.epad.common.pixelmed.TIFFMasksToDSOConverter;
import edu.stanford.epad.common.util.EPADConfig;
import edu.stanford.epad.common.util.EPADLogger;
import edu.stanford.epad.dtos.DSOEditRequest;
import edu.stanford.epad.dtos.DSOEditResult;
import edu.stanford.epad.dtos.EPADDSOFrame;
import edu.stanford.epad.dtos.EPADFrame;
import edu.stanford.epad.dtos.EPADFrameList;
import edu.stanford.epad.dtos.PNGFileProcessingStatus;
import edu.stanford.epad.dtos.internal.DICOMElement;
import edu.stanford.epad.dtos.internal.DICOMElementList;
import edu.stanford.epad.epadws.dcm4chee.Dcm4CheeDatabaseUtils;
import edu.stanford.epad.epadws.epaddb.EpadDatabase;
import edu.stanford.epad.epadws.epaddb.EpadDatabaseOperations;
import edu.stanford.epad.epadws.handlers.HandlerUtil;
import edu.stanford.epad.epadws.handlers.core.ImageReference;
import edu.stanford.epad.epadws.queries.Dcm4CheeQueries;
import edu.stanford.epad.epadws.queries.DefaultEpadOperations;
import edu.stanford.epad.epadws.queries.EpadOperations;

/**
 * Code for handling DICOM Segmentation Objects
 * 
 * 
 * @author martin
 */
public class DSOUtil
{
	private static final EPADLogger log = EPADLogger.getInstance();

	private static final String baseDicomDirectory = EPADConfig.getEPADWebServerPNGDir();

	/**
	 * Take an existing DSO and generate a new one (with new UIDs) with substituted masked frames.
	 */
	public static DSOEditResult createEditedDSO(DSOEditRequest dsoEditRequest, List<File> editFramesPNGMaskFiles)
	{
		try {
			ImageReference imageReference = new ImageReference(dsoEditRequest);
			log.info("DSO to be edited, UID:" + imageReference.seriesUID);
			DICOMElementList dicomElements = Dcm4CheeQueries.getDICOMElementsFromWADO(imageReference.studyUID, imageReference.seriesUID, imageReference.imageUID);
			String seriesDescription = null;
			String seriesUID = null;
			String instanceUID = null;
			for (DICOMElement dicomElement : dicomElements.ResultSet.Result) {
				if (dicomElement.tagCode.equalsIgnoreCase(PixelMedUtils.SeriesDescriptionCode)) {
					log.info("DSO to be edited, tag:" + dicomElement.tagName + " value:" + dicomElement.value);
					seriesDescription = dicomElement.value;
				}
			}
			if (seriesDescription != null && seriesDescription.toLowerCase().contains("epad"))
			{
				seriesUID = imageReference.seriesUID;
				instanceUID = imageReference.imageUID;
			}
			else
				seriesDescription = null;
			List<File> existingDSOTIFFMaskFiles = DSOUtil.getDSOTIFFMaskFiles(imageReference);
			List<File> editFramesTIFFMaskFiles = generateTIFFsFromPNGs(editFramesPNGMaskFiles);
			List<File> dsoTIFFMaskFiles = new ArrayList<>(existingDSOTIFFMaskFiles);
			int frameMaskFilesIndex = 0;
			for (Integer frameNumber : dsoEditRequest.editedFrameNumbers) {
				if (frameNumber >= 0 && frameNumber < existingDSOTIFFMaskFiles.size()) {
					log.info("Editing frame: " + frameNumber + " in new DSO");
					// For some reason the original DSO Masks are in reverse order
					int editMaskFileIndex = existingDSOTIFFMaskFiles.size() - frameNumber -1;
					dsoTIFFMaskFiles.set(editMaskFileIndex, editFramesTIFFMaskFiles.get(frameMaskFilesIndex++));
				} else {
					log.warning("Frame number " + frameNumber + " is out of range for DSO image " + dsoEditRequest.imageUID
							+ " in series " + dsoEditRequest.seriesUID + " which has only " + existingDSOTIFFMaskFiles.size() + " frames");
					return null;
				}
			}

			if (DSOUtil.createDSO(imageReference, dsoTIFFMaskFiles, seriesDescription, seriesUID, instanceUID))
			{
				// TODO - need to delete all temporary mask files
				return new DSOEditResult(imageReference.projectID, imageReference.subjectID, imageReference.studyUID,			
						imageReference.seriesUID, imageReference.imageUID, "");
			}
			else
				return null;
		} catch (Exception e) {
			log.warning("Error generating DSO image " + dsoEditRequest.imageUID + " in series " + dsoEditRequest.seriesUID, e);
			return null;
		}
	}

	public static boolean createDSO(ImageReference imageReference, List<File> tiffMaskFiles, String dsoSeriesDescription, String dsoSeriesUID, String dsoInstanceUID)
	{
		log.info("Generating DSO " + imageReference.imageUID + " with " + tiffMaskFiles.size() + " TIFF mask file(s)...");
		try {
			File temporaryDSOFile = File.createTempFile(imageReference.imageUID, ".dso");
			List<String> dicomFilePaths = downloadDICOMFilesForDSO(imageReference);
			log.info("Found " + dicomFilePaths.size() + " source DICOM file(s) for DSO " + imageReference.imageUID);

			log.info("Generating new edited DSO from original DSO " + imageReference.imageUID);
			TIFFMasksToDSOConverter converter = new TIFFMasksToDSOConverter();
			converter.generateDSO(files2FilePaths(tiffMaskFiles), dicomFilePaths, temporaryDSOFile.getAbsolutePath(), dsoSeriesDescription, dsoSeriesUID, dsoInstanceUID);

			log.info("Sending generated DSO " + temporaryDSOFile.getAbsolutePath() + " imageUID:" + imageReference.imageUID + " to dcm4chee...");
			DCM4CHEEUtil.dcmsnd(temporaryDSOFile.getAbsolutePath(), false);
			if (dsoSeriesUID != null)
			{
				EpadDatabaseOperations epadDatabaseOperations = EpadDatabase.getInstance().getEPADDatabaseOperations();
				epadDatabaseOperations.deleteSeries(dsoSeriesUID);
			}
			return true;
		} catch (Exception e) {
			log.warning("Error generating DSO " + imageReference.imageUID + " in series " + imageReference.seriesUID, e);
			return false;
		}
	}

	public static void writeMultiFramePNGs(File dsoFile) throws Exception
	{
		try {
			EpadDatabaseOperations databaseOperations = EpadDatabase.getInstance().getEPADDatabaseOperations();
			DicomSegmentationObject dso = new DicomSegmentationObject();
			SourceImage sourceDSOImage = dso.convert(dsoFile.getAbsolutePath());
			int numberOfFrames = sourceDSOImage.getNumberOfBufferedImages();
			AttributeList dicomAttributes = PixelMedUtils.readAttributeListFromDicomFile(dsoFile.getAbsolutePath());
			String studyUID = Attribute.getSingleStringValueOrEmptyString(dicomAttributes, TagFromName.StudyInstanceUID);
			String seriesUID = Attribute.getSingleStringValueOrEmptyString(dicomAttributes, TagFromName.SeriesInstanceUID);
			String imageUID = Attribute.getSingleStringValueOrEmptyString(dicomAttributes, TagFromName.SOPInstanceUID);

			String pngDirectoryPath = baseDicomDirectory + "/studies/" + studyUID + "/series/" + seriesUID + "/images/"
					+ imageUID + "/frames/";
			File pngFilesDirectory = new File(pngDirectoryPath);

			log.info("Writing PNGs for DSO " + imageUID + " in series " + seriesUID);

			pngFilesDirectory.mkdirs();

			for (int frameNumber = 0; frameNumber < numberOfFrames; frameNumber++) {
				BufferedImage bufferedImage = sourceDSOImage.getBufferedImage(numberOfFrames - frameNumber - 1);
				String pngFilePath = pngDirectoryPath + frameNumber + ".png";
				File pngFile = new File(pngFilePath);
				try {
					insertEpadFile(databaseOperations, pngFilePath, 0, imageUID);
					log.info("Writing PNG frame " + frameNumber + " in multi-frame image " + imageUID + " in series " + seriesUID);
					ImageIO.write(bufferedImage, "png", pngFile);
					databaseOperations.updateEpadFileRow(pngFilePath, PNGFileProcessingStatus.DONE, pngFile.length(), "");
				} catch (IOException e) {
					log.warning("Failure writing PNG file " + pngFilePath + " for frame " + frameNumber
							+ " in multi-frame image " + imageUID + " in series " + seriesUID, e);
				}
			}
			log.info("Finished writing PNGs for multi-frame DICOM " + imageUID + " in series " + seriesUID);
		} catch (DicomException e) {
			log.warning("DICOM exception writing multi-frame PNGs", e);
			throw new Exception("DICOM exception writing multi-frame PNGs", e);
		} catch (IOException e) {
			log.warning("IO exception writing multi-frame PNGs", e);
			throw new Exception("IO exception writing multi-frame PNGs", e);
		}
	}

	public static void writeDSOMaskPNGs(File dsoFile) throws Exception
	{
		try {
			EpadDatabaseOperations databaseOperations = EpadDatabase.getInstance().getEPADDatabaseOperations();
			DicomSegmentationObject dso = new DicomSegmentationObject();
			SourceImage sourceDSOImage = dso.convert(dsoFile.getAbsolutePath());
			int numberOfFrames = sourceDSOImage.getNumberOfBufferedImages();
			AttributeList dicomAttributes = PixelMedUtils.readAttributeListFromDicomFile(dsoFile.getAbsolutePath());
			String studyUID = Attribute.getSingleStringValueOrEmptyString(dicomAttributes, TagFromName.StudyInstanceUID);
			String seriesUID = Attribute.getSingleStringValueOrEmptyString(dicomAttributes, TagFromName.SeriesInstanceUID);
			String imageUID = Attribute.getSingleStringValueOrEmptyString(dicomAttributes, TagFromName.SOPInstanceUID);

			String pngMaskDirectoryPath = baseDicomDirectory + "/studies/" + studyUID + "/series/" + seriesUID + "/images/"
					+ imageUID + "/masks/";
			File pngMaskFilesDirectory = new File(pngMaskDirectoryPath);

			log.info("Writing PNG masks for DSO " + imageUID + " in series " + seriesUID + " DSOFile:" + dsoFile.getAbsolutePath() + " ...");

			pngMaskFilesDirectory.mkdirs();

			for (int frameNumber = 0; frameNumber < numberOfFrames; frameNumber++) {
				BufferedImage bufferedImage = sourceDSOImage.getBufferedImage(numberOfFrames - frameNumber - 1);

				log.info("Image dimensions - width " + bufferedImage.getWidth() + ", height " + bufferedImage.getHeight());

				BufferedImage bufferedImageWithTransparency = generateTransparentImage(bufferedImage);
				String pngMaskFilePath = pngMaskDirectoryPath + frameNumber + ".png";

				File pngMaskFile = new File(pngMaskFilePath);
				try {
					insertEpadFile(databaseOperations, pngMaskFilePath, pngMaskFile.length(), imageUID);
					log.info("Writing PNG mask file frame " + frameNumber + " for DSO " + imageUID + " in series " + seriesUID + " file:" + pngMaskFilePath);
					ImageIO.write(bufferedImageWithTransparency, "png", pngMaskFile);
					databaseOperations.updateEpadFileRow(pngMaskFilePath, PNGFileProcessingStatus.DONE, 0, "");
				} catch (IOException e) {
					log.warning("Failure writing PNG mask file " + pngMaskFilePath + " for frame " + frameNumber + " of DSO "
							+ imageUID + " in series " + seriesUID, e);
				}
			}
			log.info("... finished writing PNG masks for DSO image " + imageUID + " in series " + seriesUID);
		} catch (DicomException e) {
			log.warning("DICOM exception writing DSO PNG masks", e);
			throw new Exception("DICOM exception writing DSO PNG masks", e);
		} catch (IOException e) {
			log.warning("IO exception writing DSO PNG masks", e);
			throw new Exception("IO exception writing DSO PNG masks", e);
		}
	}

	public static boolean handleDSOFramesEdit(String projectID, String subjectID, String studyUID, String seriesUID,
			String imageUID, HttpServletRequest httpRequest, PrintWriter responseStream)
	{ // See http://www.tutorialspoint.com/servlets/servlets-file-uploading.htm
		boolean uploadError = false;

		log.info("Received DSO edit request for series " + seriesUID);

		try {
			ServletFileUpload servletFileUpload = new ServletFileUpload();
			FileItemIterator fileItemIterator = servletFileUpload.getItemIterator(httpRequest);

			DSOEditRequest dsoEditRequest = extractDSOEditRequest(fileItemIterator);

			if (dsoEditRequest != null) {
				List<File> editedFramesPNGMaskFiles = HandlerUtil.extractFiles(fileItemIterator, "DSOEditedFrame", "PNG");
				if (editedFramesPNGMaskFiles.isEmpty()) {
					log.warning("No PNG masks supplied in DSO edit request for image " + imageUID + " in series " + seriesUID);
					uploadError = true;
				} else {
					log.info("Extracted " + editedFramesPNGMaskFiles.size() + " file mask(s) for DSO edit for image " + imageUID
							+ " in  series " + seriesUID);
					DSOEditResult dsoEditResult = DSOUtil.createEditedDSO(dsoEditRequest, editedFramesPNGMaskFiles);
					if (dsoEditResult != null)
					{
						responseStream.append(dsoEditResult.toJSON());
					}
					else
					{
						log.info("Null return from createEditDSO");
						uploadError = true;
					}
				}
			} else {
				log.warning("Invalid JSON header in DSO edit request for image " + imageUID + " in  series " + seriesUID);
				uploadError = true;
			}
		} catch (IOException e) {
			log.warning("IO exception handling DSO edits for series " + seriesUID, e);
			uploadError = true;
		} catch (FileUploadException e) {
			log.warning("File upload exception handling DSO edits for series " + seriesUID, e);
			uploadError = true;
		}
		return uploadError;
	}

	private static DSOEditRequest extractDSOEditRequest(FileItemIterator fileItemIterator) throws FileUploadException,
			IOException, UnsupportedEncodingException
	{
		DSOEditRequest dsoEditRequest = null;
		Gson gson = new Gson();
		FileItemStream headerJSONItemStream = fileItemIterator.next();
		InputStream headerJSONStream = headerJSONItemStream.openStream();
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			isr = new InputStreamReader(headerJSONStream, "UTF-8");
			br = new BufferedReader(isr);

			dsoEditRequest = gson.fromJson(br, DSOEditRequest.class);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(isr);
		}
		return dsoEditRequest;
	}

	private static List<String> downloadDICOMFilesForDSO(ImageReference dsoImageReference)
	{
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		List<String> dicomFilePaths = new ArrayList<>();

		EPADFrameList frameList = epadOperations.getFrameDescriptions(dsoImageReference);
		for (EPADFrame frame : frameList.ResultSet.Result) {
			if (frame instanceof EPADDSOFrame) {
				EPADDSOFrame dsoFrame = (EPADDSOFrame)frame;
				String studyUID = dsoFrame.studyUID;
				String sourceSeriesUID = dsoFrame.sourceSeriesUID;
				String sourceImageUID = dsoFrame.sourceImageUID;

				try {
					File temporaryDICOMFile = File.createTempFile(sourceImageUID, ".dcm");
//					log.info("Downloading source DICOM file for image " + sourceImageUID + " referenced by DSO image "
//							+ dsoImageReference.imageUID);
					DCM4CHEEUtil.downloadDICOMFileFromWADO(studyUID, sourceSeriesUID, sourceImageUID, temporaryDICOMFile);
					dicomFilePaths.add(temporaryDICOMFile.getAbsolutePath());
				} catch (IOException e) {
					log.warning("Error downloading DICOM file for referenced image " + sourceImageUID + " for DSO "
							+ dsoImageReference.imageUID, e);
				}
			} else {
				log.warning("Image " + dsoImageReference.imageUID + " in series " + dsoImageReference.seriesUID
						+ " does not appear to be a DSO");
			}
		}
		return dicomFilePaths;
	}

	private static List<File> getDSOTIFFMaskFiles(ImageReference imageReference)
	{
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		List<File> dsoMaskFiles = new ArrayList<>();

		EPADFrameList frameList = epadOperations.getFrameDescriptions(imageReference);

		for (EPADFrame frame : frameList.ResultSet.Result) {
			String maskFilePath = baseDicomDirectory + frame.losslessImage;
			File maskFile = new File(maskFilePath);
			//log.info("Creating TIFF mask file " + maskFilePath + " for frame " + frame.frameNumber + " for DSO "
			//		+ imageReference.imageUID);

			try {
				BufferedImage bufferedImage = ImageIO.read(maskFile);
				File tiffFile = File.createTempFile(imageReference.imageUID + "_frame_" + frame.frameNumber + "_", ".tif");
				ImageIO.write(bufferedImage, "tif", tiffFile);
				dsoMaskFiles.add(tiffFile);
			} catch (IOException e) {
				log.warning("Error creating TIFF mask file " + maskFilePath + " for frame " + frame.frameNumber + " for DSO "
						+ imageReference.imageUID, e);
			}
		}
		return dsoMaskFiles;
	}

	private static List<File> generateTIFFsFromPNGs(List<File> pngFiles)
	{
		List<File> tiffFiles = new ArrayList<>();

		for (File pngFile : pngFiles) {
			try {
				BufferedImage bufferedImage = ImageIO.read(pngFile);
				File tiffFile = File.createTempFile(pngFile.getName(), ".tif");
				ImageIO.write(bufferedImage, "tif", tiffFile);
				tiffFiles.add(tiffFile);
			} catch (IOException e) {
				log.warning("Error creating TIFF  file from PNG " + pngFile.getAbsolutePath());
			}
		}
		return tiffFiles;
	}

	private static BufferedImage generateTransparentImage(BufferedImage source)
	{
		Image image = makeColorOpaque(source, Color.WHITE);
		BufferedImage transparent = imageToBufferedImage(image);
		Image image2 = makeColorTransparent(transparent, Color.BLACK);
		BufferedImage transparent2 = imageToBufferedImage(image2);
		return transparent2;
	}

	private static BufferedImage imageToBufferedImage(Image image)
	{
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return bufferedImage;
	}

	private static Image makeColorOpaque(BufferedImage im, final Color color)
	{
		ImageFilter filter = new RGBImageFilter() {
			public int markerRGB = color.getRGB() | 0xFF000000;

			@Override
			public final int filterRGB(int x, int y, int rgb)
			{
				if ((rgb | 0xFF000000) == markerRGB) {
					return 0xFF000000 | rgb;
				} else {
					return rgb;
				}
			}
		};

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	private static Image makeColorTransparent(BufferedImage im, final Color color)
	{
		ImageFilter filter = new RGBImageFilter() {
			public int markerRGB = color.getRGB() | 0xFF000000;

			@Override
			public final int filterRGB(int x, int y, int rgb)
			{
				if ((rgb | 0xFF000000) == markerRGB) {
					return 0x00FFFFFF & rgb;
				} else {
					return rgb;
				}
			}
		};

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	private static List<String> files2FilePaths(List<File> files)
	{
		List<String> filePaths = new ArrayList<>();

		for (File file : files)
			filePaths.add(file.getAbsolutePath());

		return filePaths;
	}

	private static void insertEpadFile(EpadDatabaseOperations epadDatabaseOperations, String outputFilePath,
			long fileSize, String imageUID)
	{
		Map<String, String> epadFilesRow = Dcm4CheeDatabaseUtils.createEPadFilesRowData(outputFilePath, fileSize, imageUID);
		epadFilesRow.put("file_status", "" + PNGFileProcessingStatus.IN_PIPELINE.getCode());
		epadDatabaseOperations.insertEpadFileRow(epadFilesRow);
	}
}
