/*******************************************************************************
 * Copyright (c) 2015 The Board of Trustees of the Leland Stanford Junior University
 * BY CLICKING ON "ACCEPT," DOWNLOADING, OR OTHERWISE USING EPAD, YOU AGREE TO THE FOLLOWING TERMS AND CONDITIONS:
 * STANFORD ACADEMIC SOFTWARE SOURCE CODE LICENSE FOR
 * "ePAD Annotation Platform for Radiology Images"
 *
 * This Agreement covers contributions to and downloads from the ePAD project ("ePAD") maintained by The Board of Trustees 
 * of the Leland Stanford Junior University ("Stanford"). 
 *
 * *	Part A applies to downloads of ePAD source code and/or data from ePAD. 
 *
 * *	Part B applies to contributions of software and/or data to ePAD (including making revisions of or additions to code 
 * and/or data already in ePAD), which may include source or object code. 
 *
 * Your download, copying, modifying, displaying, distributing or use of any ePAD software and/or data from ePAD 
 * (collectively, the "Software") is subject to Part A. Your contribution of software and/or data to ePAD (including any 
 * that occurred prior to the first publication of this Agreement) is a "Contribution" subject to Part B. Both Parts A and 
 * B shall be governed by and construed in accordance with the laws of the State of California without regard to principles 
 * of conflicts of law. Any legal action involving this Agreement or the Research Program will be adjudicated in the State 
 * of California. This Agreement shall supersede and replace any license terms that you may have agreed to previously with 
 * respect to ePAD.
 *
 * PART A. DOWNLOADING AGREEMENT - LICENSE FROM STANFORD WITH RIGHT TO SUBLICENSE ("SOFTWARE LICENSE").
 * 1. As used in this Software License, "you" means the individual downloading and/or using, reproducing, modifying, 
 * displaying and/or distributing Software and the institution or entity which employs or is otherwise affiliated with you. 
 * Stanford  hereby grants you, with right to sublicense, with respect to Stanford's rights in the Software, a 
 * royalty-free, non-exclusive license to use, reproduce, make derivative works of, display and distribute the Software, 
 * provided that: (a) you adhere to all of the terms and conditions of this Software License; (b) in connection with any 
 * copy, distribution of, or sublicense of all or any portion of the Software, the terms and conditions in this Software 
 * License shall appear in and shall apply to such copy and such sublicense, including without limitation all source and 
 * executable forms and on any user documentation, prefaced with the following words: "All or portions of this licensed 
 * product  have been obtained under license from The Board of Trustees of the Leland Stanford Junior University. and are 
 * subject to the following terms and conditions" AND any user interface to the Software or the "About" information display 
 * in the Software will display the following: "Powered by ePAD http://epad.stanford.edu;" (c) you preserve and maintain 
 * all applicable attributions, copyright notices and licenses included in or applicable to the Software; (d) modified 
 * versions of the Software must be clearly identified and marked as such, and must not be misrepresented as being the 
 * original Software; and (e) you consider making, but are under no obligation to make, the source code of any of your 
 * modifications to the Software freely available to others on an open source basis.
 *
 * 2. The license granted in this Software License includes without limitation the right to (i) incorporate the Software 
 * into your proprietary programs (subject to any restrictions applicable to such programs), (ii) add your own copyright 
 * statement to your modifications of the Software, and (iii) provide additional or different license terms and conditions 
 * in your sublicenses of modifications of the Software; provided that in each case your use, reproduction or distribution 
 * of such modifications otherwise complies with the conditions stated in this Software License.
 * 3. This Software License does not grant any rights with respect to third party software, except those rights that 
 * Stanford has been authorized by a third party to grant to you, and accordingly you are solely responsible for (i) 
 * obtaining any permissions from third parties that you need to use, reproduce, make derivative works of, display and 
 * distribute the Software, and (ii) informing your sublicensees, including without limitation your end-users, of their 
 * obligations to secure any such required permissions.
 * 4. You agree that you will use the Software in compliance with all applicable laws, policies and regulations including, 
 * but not limited to, those applicable to Personal Health Information ("PHI") and subject to the Institutional Review 
 * Board requirements of the your institution, if applicable. Licensee acknowledges and agrees that the Software is not 
 * FDA-approved, is intended only for research, and may not be used for clinical treatment purposes. Any commercialization 
 * of the Software is at the sole risk of you and the party or parties engaged in such commercialization. You further agree 
 * to use, reproduce, make derivative works of, display and distribute the Software in compliance with all applicable 
 * governmental laws, regulations and orders, including without limitation those relating to export and import control.
 * 5. You or your institution, as applicable, will indemnify, hold harmless, and defend Stanford against any third party 
 * claim of any kind made against Stanford arising out of or related to the exercise of any rights granted under this 
 * Agreement, the provision of Software, or the breach of this Agreement. Stanford provides the Software AS IS and WITH ALL 
 * FAULTS.  Stanford makes no representations and extends no warranties of any kind, either express or implied.  Among 
 * other things, Stanford disclaims any express or implied warranty in the Software:
 * (a)  of merchantability, of fitness for a particular purpose,
 * (b)  of non-infringement or 
 * (c)  arising out of any course of dealing.
 *
 * Title and copyright to the Program and any associated documentation shall at all times remain with Stanford, and 
 * Licensee agrees to preserve same. Stanford reserves the right to license the Program at any time for a fee.
 * 6. None of the names, logos or trademarks of Stanford or any of Stanford's affiliates or any of the Contributors, or any 
 * funding agency, may be used to endorse or promote products produced in whole or in part by operation of the Software or 
 * derived from or based on the Software without specific prior written permission from the applicable party.
 * 7. Any use, reproduction or distribution of the Software which is not in accordance with this Software License shall 
 * automatically revoke all rights granted to you under this Software License and render Paragraphs 1 and 2 of this 
 * Software License null and void.
 * 8. This Software License does not grant any rights in or to any intellectual property owned by Stanford or any 
 * Contributor except those rights expressly granted hereunder.
 *
 * PART B. CONTRIBUTION AGREEMENT - LICENSE TO STANFORD WITH RIGHT TO SUBLICENSE ("CONTRIBUTION AGREEMENT").
 * 1. As used in this Contribution Agreement, "you" means an individual providing a Contribution to ePAD and the 
 * institution or entity which employs or is otherwise affiliated with you.
 * 2. This Contribution Agreement applies to all Contributions made to ePAD at any time. By making a Contribution you 
 * represent that: (i) you are legally authorized and entitled by ownership or license to make such Contribution and to 
 * grant all licenses granted in this Contribution Agreement with respect to such Contribution; (ii) if your Contribution 
 * includes any patient data, all such data is de-identified in accordance with U.S. confidentiality and security laws and 
 * requirements, including but not limited to the Health Insurance Portability and Accountability Act (HIPAA) and its 
 * regulations, and your disclosure of such data for the purposes contemplated by this Agreement is properly authorized and 
 * in compliance with all applicable laws and regulations; and (iii) you have preserved in the Contribution all applicable 
 * attributions, copyright notices and licenses for any third party software or data included in the Contribution.
 * 3. Except for the licenses you grant in this Agreement, you reserve all right, title and interest in your Contribution.
 * 4. You hereby grant to Stanford, with the right to sublicense, a perpetual, worldwide, non-exclusive, no charge, 
 * royalty-free, irrevocable license to use, reproduce, make derivative works of, display and distribute the Contribution. 
 * If your Contribution is protected by patent, you hereby grant to Stanford, with the right to sublicense, a perpetual, 
 * worldwide, non-exclusive, no-charge, royalty-free, irrevocable license under your interest in patent rights embodied in 
 * the Contribution, to make, have made, use, sell and otherwise transfer your Contribution, alone or in combination with 
 * ePAD or otherwise.
 * 5. You acknowledge and agree that Stanford ham may incorporate your Contribution into ePAD and may make your 
 * Contribution as incorporated available to members of the public on an open source basis under terms substantially in 
 * accordance with the Software License set forth in Part A of this Agreement. You further acknowledge and agree that 
 * Stanford shall have no liability arising in connection with claims resulting from your breach of any of the terms of 
 * this Agreement.
 * 6. YOU WARRANT THAT TO THE BEST OF YOUR KNOWLEDGE YOUR CONTRIBUTION DOES NOT CONTAIN ANY CODE OBTAINED BY YOU UNDER AN 
 * OPEN SOURCE LICENSE THAT REQUIRES OR PRESCRIBES DISTRBUTION OF DERIVATIVE WORKS UNDER SUCH OPEN SOURCE LICENSE. (By way 
 * of non-limiting example, you will not contribute any code obtained by you under the GNU General Public License or other 
 * so-called "reciprocal" license.)
 *******************************************************************************/
package edu.stanford.epad.epadws.controllers;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import edu.stanford.epad.common.util.EPADLogger;
import edu.stanford.epad.dtos.EPADEventLogList;
import edu.stanford.epad.dtos.EPADObjectList;
import edu.stanford.epad.dtos.EPADUser;
import edu.stanford.epad.dtos.EPADUserList;
import edu.stanford.epad.dtos.RemotePAC;
import edu.stanford.epad.dtos.RemotePACEntity;
import edu.stanford.epad.dtos.RemotePACEntityList;
import edu.stanford.epad.dtos.RemotePACList;
import edu.stanford.epad.dtos.RemotePACQueryConfig;
import edu.stanford.epad.dtos.RemotePACQueryConfigList;
import edu.stanford.epad.epadws.controllers.exceptions.NotFoundException;
import edu.stanford.epad.epadws.handlers.HandlerUtil;
import edu.stanford.epad.epadws.handlers.core.UsersRouteTemplates;
import edu.stanford.epad.epadws.models.RemotePACQuery;
import edu.stanford.epad.epadws.models.WorkList;
import edu.stanford.epad.epadws.queries.DefaultEpadOperations;
import edu.stanford.epad.epadws.queries.EpadOperations;
import edu.stanford.epad.epadws.security.EPADSession;
import edu.stanford.epad.epadws.service.DefaultWorkListOperations;
import edu.stanford.epad.epadws.service.EpadWorkListOperations;
import edu.stanford.epad.epadws.service.RemotePACService;
import edu.stanford.epad.epadws.service.SessionService;

@RestController
@RequestMapping("/users")
public class UserController {
	private static final EPADLogger log = EPADLogger.getInstance();
 
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public EPADUserList getEPADUsers( 
										@RequestParam(value="includeSystemUsage", defaultValue="false") boolean includeSystemUsage,											
										HttpServletRequest request, 
								        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		EPADUserList userlist = epadOperations.getUserDescriptions(username, sessionID,includeSystemUsage);
		return userlist;
	}
 
	@RequestMapping(value = "/{user}", method = RequestMethod.GET)
	public EPADUser getEPADUser( 
											@PathVariable String user,
											@RequestParam(value="includeSystemUsage", defaultValue="false") boolean includeSystemUsage,											
											HttpServletRequest request, 
									        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		EPADUser euser = epadOperations.getUserDescription(username, user, sessionID, includeSystemUsage);
		if (euser == null)
			throw new NotFoundException("User " + user + " not found");
		return euser;
	}
	 
	@RequestMapping(value = "/{user}/sessions/", method = RequestMethod.GET)
	public Collection<EPADSession> getEPADUserSessions( 
											@PathVariable String user,
											HttpServletRequest request, 
									        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		EPADUser euser = epadOperations.getUserDescription(username, user, sessionID, false);
		if (euser == null)
			throw new NotFoundException("User " + user + " not found");
		Collection<EPADSession> sessions = epadOperations.getCurrentSessions(user);
		return sessions;
	}
	 
	@RequestMapping(value = "/{user}/eventlogs/", method = RequestMethod.GET)
	public EPADEventLogList getEPADUserEventLogs( 
											@PathVariable String user,
											@RequestParam(value="start", defaultValue = "0") int start,
											@RequestParam(value="count", defaultValue = "0") int count,
											HttpServletRequest request, 
									        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		EPADUser euser = epadOperations.getUserDescription(username, user, sessionID, false);
		if (euser == null)
			throw new NotFoundException("User " + user + " not found");
		EPADEventLogList logs = epadOperations.getEventLogs(username, user, start, count);
		return logs;
	}
	 
	@RequestMapping(value = "/{user}/taskstatus/", method = RequestMethod.GET)
	public EPADObjectList getEPADUserTaskStatus( 
											@PathVariable String user,
											HttpServletRequest request, 
									        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		EPADUser euser = epadOperations.getUserDescription(username, user, sessionID, false);
		if (euser == null)
			throw new NotFoundException("User " + user + " not found");
		EPADObjectList tasks = epadOperations.getTaskStatuses(username, user);
		return tasks;
	}
	 
	@RequestMapping(value = "/{user}/reviewers/", method = RequestMethod.GET)
	public EPADUserList getUserReviewers( 
											@PathVariable String user,
											HttpServletRequest request, 
									        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		return epadOperations.getReviewers(username, user, sessionID);
	}
	 
	@RequestMapping(value = "/{user}/reviewees/", method = RequestMethod.GET)
	public EPADUserList getUserReviewees( 
											@PathVariable String user,
											HttpServletRequest request, 
									        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		return epadOperations.getReviewees(username, user, sessionID);
	}
	 
	@RequestMapping(value = "/{user}/{username}", method = {RequestMethod.PUT,RequestMethod.POST})
	public void createUser( 
											@PathVariable String username,
											@RequestParam(value="firstname") String firstname,
											@RequestParam(value="lastname") String lastname,
											@RequestParam(value="email") String email,
											@RequestParam(value="password") String password,
											@RequestParam(value="oldpassword") String oldpassword,
											@RequestParam(value="colorpreference") String colorpreference,
											@RequestParam(value="addPermissions") String[] addPermissions,
											@RequestParam(value="removePermissions") String[] removePermissions,
											@RequestParam(value="enable") String enable,
											HttpServletRequest request, 
									        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String loggedInUser = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		epadOperations.createOrModifyUser(loggedInUser, username, firstname, lastname, email, password, oldpassword, colorpreference, addPermissions, removePermissions);
		if ("true".equalsIgnoreCase(enable))
			epadOperations.enableUser(loggedInUser, username);
		else if ("false".equalsIgnoreCase(enable))
			epadOperations.disableUser(loggedInUser, username);
	}
	
	 
	@RequestMapping(value = "/{user}/{username}", method = RequestMethod.DELETE)
	public void deleteUser( 
			@PathVariable String username,
			HttpServletRequest request, 
	        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String loggedInUser = SessionService.getUsernameForSession(sessionID);
		EpadOperations epadOperations = DefaultEpadOperations.getInstance();
		epadOperations.deleteUser(loggedInUser, username);
	}
	
	@RequestMapping(value = "/{reader}/worklists/{workListID:.+}", method = RequestMethod.PUT)
	public void createUserWorkList( 
										@PathVariable String projectID,
										@PathVariable String reader,
										@PathVariable String workListID,
										@RequestParam(value="name", required=false) String name,
										@RequestParam(value="description", required=false) String description,
										@RequestParam(value="dueDate", required=false) String dueDate,
										@RequestParam(value="status", required=false) String wlstatus,
										@RequestParam(value="started", required=false) Boolean started,
										@RequestParam(value="completed", required=false) Boolean completed,
										HttpServletRequest request, 
								        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadWorkListOperations worklistOperations = DefaultWorkListOperations.getInstance();
		WorkList worklist = worklistOperations.getWorkList(workListID);
		if (worklist == null)
		{
			worklist = worklistOperations.createWorkList(username, reader, workListID, name, description, null, getDate(dueDate));
		}
		else
		{
			if (description != null || dueDate != null)
				worklistOperations.updateWorkList(username, reader, workListID, name, description, null, getDate(dueDate));
		}
		if (wlstatus != null || started != null || completed != null)
		{
			log.info("WorklistID:" + workListID + " status:" + wlstatus + " started:" + started + " completed:" + completed);
			worklistOperations.setWorkListStatus(reader, workListID, wlstatus, started, completed);
		}
	}
	
	@RequestMapping(value = "/{reader}/worklists/", method = {RequestMethod.POST,RequestMethod.PUT})
	public void createUserWorkList( 
										@PathVariable String projectID,
										@PathVariable String reader,
										@RequestParam(value="description", required=false) String description,
										@RequestParam(value="name", required=false) String name,
										@RequestParam(value="dueDate", required=false) String dueDate,
										HttpServletRequest request, 
								        HttpServletResponse response) throws Exception {
		String sessionID = SessionService.getJSessionIDFromRequest(request);
		String username = SessionService.getUsernameForSession(sessionID);
		EpadWorkListOperations worklistOperations = DefaultWorkListOperations.getInstance();
		worklistOperations.createWorkList(username, reader, null, name, description, null, getDate(dueDate));
	}
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
	private Date getDate(String dateStr)
	{
		try
		{
			return dateformat.parse(dateStr);
		}
		catch (Exception x)
		{
			return null;
		}
	}
	
	
//	
//} else if (HandlerUtil.matchesTemplate(UsersRouteTemplates.USER_WORKLIST, pathInfo)) {
//	Map<String, String> templateMap = HandlerUtil.getTemplateMap(UsersRouteTemplates.USER_WORKLIST, pathInfo);
//	String reader = HandlerUtil.getTemplateParameter(templateMap, "username");
//	String workListID = HandlerUtil.getTemplateParameter(templateMap, "worklistID");
//	String wlstatus = httpRequest.getParameter("status");
//	boolean started = "true".equalsIgnoreCase(httpRequest.getParameter("started"));
//	boolean completed = "true".equalsIgnoreCase(httpRequest.getParameter("completed"));
//	worklistOperations.setWorkListStatus(reader, workListID, wlstatus, started, completed);
//	statusCode = HttpServletResponse.SC_OK;
//	
//} else if (HandlerUtil.matchesTemplate(UsersRouteTemplates.USER_SUBJECT, pathInfo)) {
//	Map<String, String> templateMap = HandlerUtil.getTemplateMap(UsersRouteTemplates.USER_SUBJECT, pathInfo);
//	String reader = HandlerUtil.getTemplateParameter(templateMap, "username");
//	String subjectID = HandlerUtil.getTemplateParameter(templateMap, "subjectID");
//	String wlstatus = httpRequest.getParameter("status");
//	boolean started = "true".equalsIgnoreCase(httpRequest.getParameter("started"));
//	boolean completed = "true".equalsIgnoreCase(httpRequest.getParameter("completed"));
//	Set<WorkList> wls = worklistOperations.getWorkListsForUserBySubject(username, subjectID);
//	for (WorkList wl: wls)
//		worklistOperations.setWorkListSubjectStatus(reader, wl.getWorkListID(), subjectID, wlstatus, started, completed);
//	statusCode = HttpServletResponse.SC_OK;
//
//} else if (HandlerUtil.matchesTemplate(UsersRouteTemplates.USER_STUDY, pathInfo)) {
//	Map<String, String> templateMap = HandlerUtil.getTemplateMap(UsersRouteTemplates.USER_STUDY, pathInfo);
//	String reader = HandlerUtil.getTemplateParameter(templateMap, "username");
//	String studyUID = HandlerUtil.getTemplateParameter(templateMap, "studyUID");
//	String wlstatus = httpRequest.getParameter("status");
//	boolean started = "true".equalsIgnoreCase(httpRequest.getParameter("started"));
//	boolean completed = "true".equalsIgnoreCase(httpRequest.getParameter("completed"));
//	Set<WorkList> wls = worklistOperations.getWorkListsForUserByStudy(username, studyUID);
//	for (WorkList wl: wls)
//		worklistOperations.setWorkListSubjectStatus(reader, wl.getWorkListID(), studyUID, wlstatus, started, completed);
//	statusCode = HttpServletResponse.SC_OK;
//
//} else if (HandlerUtil.matchesTemplate(UsersRouteTemplates.USER_REVIEWEE, pathInfo)) {
//	Map<String, String> templateMap = HandlerUtil.getTemplateMap(UsersRouteTemplates.USER_REVIEWEE, pathInfo);
//	String reviewer = HandlerUtil.getTemplateParameter(templateMap, "username");
//	String reviewee = HandlerUtil.getTemplateParameter(templateMap, "reviewee");
//	epadOperations.addReviewee(username, reviewer, reviewee);
//	statusCode = HttpServletResponse.SC_OK;
//
//} else if (HandlerUtil.matchesTemplate(UsersRouteTemplates.USER_REVIEWER, pathInfo)) {
//	Map<String, String> templateMap = HandlerUtil.getTemplateMap(UsersRouteTemplates.USER_REVIEWEE, pathInfo);
//	String reviewee = HandlerUtil.getTemplateParameter(templateMap, "username");
//	String reviewer = HandlerUtil.getTemplateParameter(templateMap, "reviewer");
//	epadOperations.addReviewer(username, reviewee, reviewer);
//	statusCode = HttpServletResponse.SC_OK;


}
