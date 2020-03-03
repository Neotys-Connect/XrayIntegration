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
    Optional<String> version=Optional.empty();
    Optional<String> revision=Optional.empty();
    Optional<String> testPlan=Optional.empty();
    Optional<String> testEnvironment=Optional.empty();
    Optional<HashMap<String,String>> customFields=Optional.empty();
    Optional<List<String>> tags =Optional.empty();
    Optional<String> issuetype=Optional.empty();
    Optional<String> fixVersions=Optional.empty();


    public NeoLoadXrayDescription(String project, Optional<String> version, Optional<String> revision, Optional<String> testPlan, Optional<String> testEnvironment, Optional<HashMap<String, String>> customFields, Optional<List<String>> tags, Optional<String> issuetype, Optional<String> fixVersions) {
        this.project = project;
        this.version = version;
        this.revision = revision;
        this.testPlan = testPlan;
        this.testEnvironment = testEnvironment;
        this.customFields = customFields;
        this.tags = tags;
        this.issuetype = issuetype;
        this.fixVersions = fixVersions;
    }

    public Optional<String> getFixVersions() {
        return fixVersions;
    }

    public void setFixVersions(Optional<String> fixVersions) {
        this.fixVersions = fixVersions;
    }

    public Optional<String> getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(Optional<String> issuetype) {
        this.issuetype = issuetype;
    }

    public Optional<List<String>> getTags() {
        return tags;
    }

    public void setTags(Optional<List<String>> tags) {
        this.tags = tags;
    }

    public Optional<HashMap<String, String>> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Optional<HashMap<String, String>> customFields) {
        this.customFields = customFields;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Optional<String> getVersion() {
        return version;
    }

    public void setVersion(Optional<String> version) {
        this.version = version;
    }

    public Optional<String> getRevision() {
        return revision;
    }

    public void setRevision(Optional<String> revision) {
        this.revision = revision;
    }

    public Optional<String> getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(Optional<String> testPlan) {
        this.testPlan = testPlan;
    }

    public Optional<String> getTestEnvironment() {
        return testEnvironment;
    }

    public void setTestEnvironment(Optional<String> testEnvironment) {
        this.testEnvironment = testEnvironment;
    }
}
