package com.neotys.xray.datamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class NeoLoadXrayDescription {

    //---------------
    /*          field description stored
    {
     project : ,
    version :
    testplan:
    revision:
    testEnvironment:}
    */
    //--------------
    String project;
    String version;
    String revision;
    String  testPlan;
    String testEnvironment;
    HashMap<String,String> customFields;
    List<String> tags;
    String fixVersions;

    public NeoLoadXrayDescription(String project, String version, String revision, String testPlan, String testEnvironment, HashMap<String, String> customFields, List<String> tags, String fixVersions) {
        this.project = project;
        this.version = version;
        this.revision = revision;
        this.testPlan = testPlan;
        this.testEnvironment = testEnvironment;
        this.customFields = customFields;
        this.tags = tags;
        this.fixVersions = fixVersions;
    }

    public NeoLoadXrayDescription(String project, com.google.common.base.Optional<String> version, com.google.common.base.Optional<String> revision,  com.google.common.base.Optional<String> testplans, com.google.common.base.Optional<String> environments, com.google.common.base.Optional<HashMap<String, String>> customFields, com.google.common.base.Optional<List<String>> optinalListofTags,  com.google.common.base.Optional<String> fixVersions) {
        this.project = project;
        if(version.isPresent())
            this.version=version.get();
        else
            this.version=null;
        if(revision.isPresent())
            this.revision=revision.get();
        else
            this.revision=null;

        if(testplans.isPresent())
            this.testPlan=testplans.get();
        else
            this.testPlan=null;

        if(environments.isPresent())
            this.testEnvironment=environments.get();
        else
            this.testEnvironment=null;

        if(customFields.isPresent())
            this.customFields=customFields.get();

        if(optinalListofTags.isPresent())
            this.tags=optinalListofTags.get();
        else
            this.tags=null;

        if(fixVersions.isPresent())
            this.fixVersions=fixVersions.get();
        else
            this.fixVersions=null;

    }

    public String getFixVersions() {
        return fixVersions;
    }

    public void setFixVersions(String fixVersions) {
        this.fixVersions = fixVersions;
    }


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public HashMap<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(HashMap<String, String> customFields) {
        this.customFields = customFields;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(String testPlan) {
        this.testPlan = testPlan;
    }

    public String getTestEnvironment() {
        return testEnvironment;
    }

    public void setTestEnvironment(String testEnvironment) {
        this.testEnvironment = testEnvironment;
    }
}
