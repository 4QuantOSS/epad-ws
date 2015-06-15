package edu.stanford.epad.epadws.models;

//Copyright (c) 2014 The Board of Trustees of the Leland Stanford Junior University
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification, are permitted provided that
//the following conditions are met:
//
//Redistributions of source code must retain the above copyright notice, this list of conditions and the following
//disclaimer.
//
//Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
//following disclaimer in the documentation and/or other materials provided with the distribution.
//
//Neither the name of The Board of Trustees of the Leland Stanford Junior University nor the names of its
//contributors (Daniel Rubin, et al) may be used to endorse or promote products derived from this software without
//specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
//INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
//USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

import java.util.Date;

import edu.stanford.epad.epadws.models.dao.AbstractDAO;

public class NonDicomSeries extends AbstractDAO {

	long id;
	long studyId;
	String seriesUID;
	String description;
	Date seriesDate;
	String modality;
	String referencedSeries; // If modality is "SEG" 
	String creator;
	Date createdTime;
	Date updateTime;

	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStudyId() {
		return studyId;
	}

	public void setStudyId(long studyId) {
		this.studyId = studyId;
	}

	public String getSeriesUID() {
		return seriesUID;
	}

	public void setSeriesUID(String seriesUID) {
		this.seriesUID = seriesUID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getSeriesDate() {
		return seriesDate;
	}

	public void setSeriesDate(Date seriesDate) {
		this.seriesDate = seriesDate;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getReferencedSeries() {
		return referencedSeries;
	}

	public void setReferencedSeries(String referencedSeries) {
		this.referencedSeries = referencedSeries;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public final static String DBTABLE = "nondicom_series";
	public final static String[][] DBCOLUMNS = {
        {"id","long","id","Id"},
        {"studyId","long","study_id","integer"},
        {"seriesUID","String","seriesUID","varchar"},
        {"description","String","description","varchar"},
        {"seriesDate","Date","seriesdate","date"},
        {"modality","String","modality","varchar"},
        {"referencedSeries","String","referencedseries","varchar"},
        {"creator","String","creator","varchar"},
        {"createdTime","Date","createdtime","timestamp"},
        {"updateTime","Date","updatetime","timestamp"},	
	};

	@Override
	public String returnDBTABLE() {
		return DBTABLE;
	}

	@Override
	public String[][] returnDBCOLUMNS() {
		return DBCOLUMNS;
	}

}
