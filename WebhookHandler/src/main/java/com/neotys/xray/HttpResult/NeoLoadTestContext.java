package com.neotys.xray.HttpResult;

import com.neotys.ascode.swagger.client.model.ArrayOfSLAGlobalIndicatorDefinition;
import com.neotys.ascode.swagger.client.model.ArrayOfSLAPerIntervalDefinition;
import com.neotys.ascode.swagger.client.model.ArrayOfSLAPerTestDefinition;
import com.neotys.ascode.swagger.client.model.TestStatistics;
import com.neotys.xray.Logger.NeoLoadLogger;
import com.neotys.xray.datamodel.NeoLoadXrayDescription;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;

import java.util.Optional;

import static com.neotys.xray.conf.Constants.HTTPS;
import static com.neotys.xray.conf.Constants.NEOLAOD_WEB_LASTPART_URL;
import static com.neotys.xray.conf.Constants.NEOLAOD_WEB_URL;

public class NeoLoadTestContext {
    private String testid;
    private String projectName;
    private String scenarioName;
    private String testname;
    private long teststart;
    private long testEnd;
    private Optional<ArrayOfSLAGlobalIndicatorDefinition> arrayOfSLAGlobalIndicatorDefinitionOptional;
    private Optional<ArrayOfSLAPerTestDefinition> arrayOfSLAPerTestDefinition;
    private Optional<ArrayOfSLAPerIntervalDefinition> arrayOfSLAPerIntervalDefinition;
    private NeoLoadXrayDescription description;
    private String status;
    private String neoLoadWebURL;
    private TestStatistics statistics;
    private Optional<String> customRevistionid;
    private Optional<String> customPlanID;
    private Optional<String> customEnvironmentid;
    private NeoLoadLogger logger;

    public NeoLoadTestContext(String testid, String projectName, String scenarioName, String testname, long teststart, long testEnd, Optional<ArrayOfSLAGlobalIndicatorDefinition> arrayOfSLAGlobalIndicatorDefinitionOptional, Optional<ArrayOfSLAPerTestDefinition> arrayOfSLAPerTestDefinition, Optional<ArrayOfSLAPerIntervalDefinition> arrayOfSLAPerIntervalDefinition, NeoLoadXrayDescription description, String status, String neoLoadWebURL, TestStatistics statistics, Optional<String> customRevistionid, Optional<String> customPlanID, Optional<String> customEnvironmentid, NeoLoadLogger logger) {
        this.testid = testid;
        this.projectName = projectName;
        this.scenarioName = scenarioName;
        this.testname = testname;
        this.teststart = teststart;
        this.testEnd = testEnd;
        this.arrayOfSLAGlobalIndicatorDefinitionOptional = arrayOfSLAGlobalIndicatorDefinitionOptional;
        this.arrayOfSLAPerTestDefinition = arrayOfSLAPerTestDefinition;
        this.arrayOfSLAPerIntervalDefinition = arrayOfSLAPerIntervalDefinition;
        this.description = description;
        this.status = status;
        this.neoLoadWebURL=neoLoadWebURL;
        this.statistics=statistics;
        this.customEnvironmentid=customEnvironmentid;
        this.customPlanID=customPlanID;
        this.customRevistionid=customRevistionid;
        this.logger=logger;
    }

    public NeoLoadLogger getLogger() {
        return logger;
    }

    public void setLogger(NeoLoadLogger logger) {
        this.logger = logger;
    }

    public Optional<String> getCustomRevistionid() {
        return customRevistionid;
    }

    public void setCustomRevistionid(Optional<String> customRevistionid) {
        this.customRevistionid = customRevistionid;
    }

    public Optional<String> getCustomPlanID() {
        return customPlanID;
    }

    public void setCustomPlanID(Optional<String> customPlanID) {
        this.customPlanID = customPlanID;
    }

    public Optional<String> getCustomEnvironmentid() {
        return customEnvironmentid;
    }

    public void setCustomEnvironmentid(Optional<String> customEnvironmentid) {
        this.customEnvironmentid = customEnvironmentid;
    }

    public String getNeoLoadWebURL() {
        return neoLoadWebURL;
    }

    public void setNeoLoadWebURL(String neoLoadWebURL) {
        this.neoLoadWebURL = neoLoadWebURL;
    }

    public TestStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(TestStatistics statistics) {
        this.statistics = statistics;
    }

    public String gettestURL()
    {
        return HTTPS+getNeoLoadWebURL()+NEOLAOD_WEB_URL+testid+NEOLAOD_WEB_LASTPART_URL;
    }

    public String getTestid() {
        return testid;
    }

    public void setTestid(String testid) {
        this.testid = testid;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getTestname() {
        return testname;
    }

    public void setTestname(String testname) {
        this.testname = testname;
    }

    public long getTeststart() {
        return teststart;
    }

    public void setTeststart(long teststart) {
        this.teststart = teststart;
    }

    public long getTestEnd() {
        return testEnd;
    }

    public NeoLoadXrayDescription getDescription() {
        return description;
    }

    public void setDescription(NeoLoadXrayDescription description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTestEnd(long testEnd) {
        this.testEnd = testEnd;
    }

    public Optional<ArrayOfSLAGlobalIndicatorDefinition> getArrayOfSLAGlobalIndicatorDefinitionOptional() {
        return arrayOfSLAGlobalIndicatorDefinitionOptional;
    }

    public void setArrayOfSLAGlobalIndicatorDefinitionOptional(Optional<ArrayOfSLAGlobalIndicatorDefinition> arrayOfSLAGlobalIndicatorDefinitionOptional) {
        this.arrayOfSLAGlobalIndicatorDefinitionOptional = arrayOfSLAGlobalIndicatorDefinitionOptional;
    }

    public Optional<ArrayOfSLAPerTestDefinition> getArrayOfSLAPerTestDefinition() {
        return arrayOfSLAPerTestDefinition;
    }

    public void setArrayOfSLAPerTestDefinition(Optional<ArrayOfSLAPerTestDefinition> arrayOfSLAPerTestDefinition) {
        this.arrayOfSLAPerTestDefinition = arrayOfSLAPerTestDefinition;
    }

    public Optional<ArrayOfSLAPerIntervalDefinition> getArrayOfSLAPerIntervalDefinition() {
        return arrayOfSLAPerIntervalDefinition;
    }

    public void setArrayOfSLAPerIntervalDefinition(Optional<ArrayOfSLAPerIntervalDefinition> arrayOfSLAPerIntervalDefinition) {
        this.arrayOfSLAPerIntervalDefinition = arrayOfSLAPerIntervalDefinition;
    }
}
