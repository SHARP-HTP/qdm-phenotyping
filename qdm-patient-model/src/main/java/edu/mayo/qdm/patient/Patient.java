/*
 * Copyright: (c) 2004-2012 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.qdm.patient;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Herman and Darin IHC adapted by Dingcheng Li
 */
public class Patient {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    static {
        JSON_MAPPER.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
    }

    private Set<Lab> labs = new HashSet<Lab>();
    private Set<Encounter> encounters = new HashSet<Encounter>();
    private Set<Medication> medications = new HashSet<Medication>();
    private Set<Diagnosis> diagnoses = new HashSet<Diagnosis>();
    private Set<Procedure> procedures = new HashSet<Procedure>();
    private Set<Exception> patExceptions = new HashSet<Exception>();
    private Set<Eligibility> eligibilities = new HashSet<Eligibility>();
    private Set<RiskCategoryAssessment> riskCategoryAssessments = new HashSet<RiskCategoryAssessment>();
    private Set<Symptom> symptoms = new HashSet<Symptom>();
    private Set<PhysicalExamFinding> physicalExamFindings = new HashSet<PhysicalExamFinding>();
    private Date birthdate;
    private Integer age;
    private Boolean consent;
    private Gender sex;
    private String sourcePid;
    private Race race;
    private Ethnicity ethnicity;

    /*
     * For JSON only
     */
    private Patient() {
        super();
    }

    public Patient(String sourcePid) {
        super();
        this.sourcePid = sourcePid;
    }

    public void setSourcePid(String sourcePid) {
        this.sourcePid = sourcePid;
    }

    public String getSourcePid() {
        return this.sourcePid;
    }

    public void setBirthdate(Date birthdate) {
        int calculatedAge = calculateAge(birthdate);
        if (this.age == null) {
            this.age = calculatedAge;
        } else {
            if (this.age != calculatedAge) {
                throw new IllegalStateException("Previously specified age and input birthdate do not match.");
            }
        }
        this.birthdate = birthdate;
    }

    public Date getBirthdate() {
        return this.birthdate;
    }

    //it seems that the following is redundant. But I want to handle cases there only age information is given
    public void setAge(int age) {
        if (this.birthdate != null) {
            int calculatedAge = calculateAge(this.birthdate);
            if (age != calculatedAge) {
                throw new IllegalStateException("Previously specified birthdate and input age do not match.");
            }
        }
        this.age = age;
    }

    public Integer getAge() {
        return this.age;
    }

    public Boolean getConsent() {
        return this.consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
    }

    public Set<Lab> getLabs() {
        return this.labs;
    }

    public void addLab(Lab l) {
        if (l == null) {
            throw new IllegalArgumentException();
        }
        getLabs().add(l);
    }

    public Set<Encounter> getEncounters() {
        return this.encounters;
    }

    /**
     * @param e
     */
    public void addEncounter(Encounter e) {
        if (e == null) {
            throw new IllegalArgumentException();
        }
        getEncounters().add(e);
    }

    /**
     * the function is similar to above one.
     *
     * @param encounters
     */
    public void setEncounters(Set<Encounter> encounters) {
        this.encounters = encounters;
    }

    public Set<Medication> getMedications() {
        return this.medications;
    }

    public void addMedication(Medication m) {
        if (m == null) {
            throw new IllegalArgumentException();
        }
        getMedications().add(m);
    }

    public Set<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void addDiagnosis(Diagnosis d) {
        if (d == null) {
            throw new IllegalArgumentException();
        }
        getDiagnoses().add(d);
    }

    public Set<PhysicalExamFinding> getPhysicalExamFindings() {
        return physicalExamFindings;
    }

    public void addPhysicalExamFinding(PhysicalExamFinding physicalExamFinding) {
        if (physicalExamFinding == null) {
            throw new IllegalArgumentException();
        }
        getPhysicalExamFindings().add(physicalExamFinding);
    }

    public Set<Procedure> getProcedures() {
        return procedures;
    }

    public void addProcedure(Procedure procedure) {
        if (procedure == null) {
            throw new IllegalArgumentException();
        }
        getProcedures().add(procedure);
    }

    public Set<RiskCategoryAssessment> getRiskCategoryAssessments() {
        return this.riskCategoryAssessments;
    }

    public void addRiskCategoryAssessment(RiskCategoryAssessment riskCategoryAssessment) {
        if (riskCategoryAssessment == null) {
            throw new IllegalArgumentException();
        }
        getRiskCategoryAssessments().add(riskCategoryAssessment);
    }

    public Set<Exception> getExceptions() {
        return patExceptions;
    }

    public void addException(Exception exception) {
        if (exception == null) {
            throw new IllegalArgumentException();
        }
        getExceptions().add(exception);
    }

    public Set<Symptom> getSymptoms() {
        return symptoms;
    }

    public void addSymptom(Symptom symptom) {
        if (symptom == null) {
            throw new IllegalArgumentException();
        }
        getSymptoms().add(symptom);
    }

    public void setSex(Gender sex) {
        this.sex = sex;
    }

    public Gender getSex() {
        return this.sex;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Race getRace() {
        return this.race;
    }

    public void setEthnicity(Ethnicity ethnicity) {
        this.ethnicity = ethnicity;
    }

    public Ethnicity getEthnicity() {
        return this.ethnicity;
    }

    public Set<Eligibility> getEligibilities() {
        return eligibilities;
    }

    public void addEligibility(Eligibility e) {
        if (e == null) {
            throw new IllegalArgumentException();
        }
        this.getEligibilities().add(e);
    }

    protected static int calculateAge(Date birthday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthday);

        return getAge(calendar);
    }

    private static int getAge(Calendar born) {
        Calendar age = Calendar.getInstance();
        age.add(Calendar.YEAR, -born.get(Calendar.YEAR));
        age.add(Calendar.MONTH, -born.get(Calendar.MONTH));
        age.add(Calendar.DATE, -born.get(Calendar.DATE));
        return age.get(Calendar.YEAR);
    }

    public String toJson() {
        try {
            return JSON_MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException("Cannot export Patient JSON.", e);
        }
    }

    public static Patient fromJson(String json) {
        try {
            return JSON_MAPPER.readValue(json, Patient.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load Patient JSON.", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        if (sourcePid != null ? !sourcePid.equals(patient.sourcePid) : patient.sourcePid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sourcePid != null ? sourcePid.hashCode() : 0;
    }
}
