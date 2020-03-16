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
    String testPlan;
    String testEnvironment;
    HashMap<String,String> customFields;
    List<String> tags;
    String issuetype;
    String fixVersions;


    public NeoLoadXrayDescription(String project, String version, String revision, String testPlan, String testEnvironment, HashMap<String, String> customFields, List<String> tags, String issuetype, String fixVersions) {
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
               return Optional.ofNullable(fixVersions).filter(o->!o.isEmpty());
    }

    public void setFixVersions(String fixVersions) {
        this.fixVersions = fixVersions;
    }

    public Optional<String> getIssuetype() {
        return Optional.ofNullable(issuetype).filter(o->!o.isEmpty());
    }

    public void setIssuetype(String issuetype) {
        this.issuetype = issuetype;
    }

    public Optional<List<String>> getTags() {
        return Optional.ofNullable(tags).filter(o->!o.isEmpty());
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Optional<HashMap<String, String>> getCustomFields() {
        return Optional.ofNullable(customFields).filter(o->!o.isEmpty());
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

    public Optional<String> getVersion() {
        return Optional.ofNullable(version).filter(o->!o.isEmpty());
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Optional<String> getRevision() {
        return Optional.ofNullable(revision).filter(o->!o.isEmpty());
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Optional<String> getTestPlan() {
        return Optional.ofNullable(testPlan).filter(o->!o.isEmpty());
    }

    public void setTestPlan(String testPlan) {
        this.testPlan = testPlan;
    }

    public Optional<String> getTestEnvironment() {
        return Optional.ofNullable(testEnvironment).filter(o->!o.isEmpty());
    }

    public void setTestEnvironment(String testEnvironment) {
        this.testEnvironment = testEnvironment;
    }
}
