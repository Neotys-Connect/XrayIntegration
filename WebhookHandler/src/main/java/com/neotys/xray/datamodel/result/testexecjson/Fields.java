package com.neotys.xray.datamodel.result.testexecjson;

import com.neotys.ascode.swagger.client.model.TestStatistics;
import com.neotys.xray.HttpResult.NeoLoadTestContext;

import java.util.*;

import static com.neotys.xray.conf.Constants.ISSUE_TYPE;
import static com.neotys.xray.conf.NeoLoadMetrics.*;

public class Fields {
    Project project;
    IssueType issuetype;
    List<FixVersion> fixVersions;
    String summary;
    String description;
    HashMap<String,String> customfields;


    public Fields(Project project, IssueType issuetype, List<FixVersion> fixVersions, String summary, String description, HashMap<String, String> customfields) {
        this.project = project;
        this.issuetype = issuetype;
        this.fixVersions = fixVersions;
        this.summary = summary;
        this.description = description;
        this.customfields = customfields;

    }

    public HashMap<String, String> getCustomfields() {
        return customfields;
    }

    public void setCustomfields(HashMap<String, String> customfields) {
        this.customfields = customfields;
    }

    public void addCustomField(HashMap<String,String> field)
    {
        if(this.customfields!=null)
        {
            this.customfields.putAll(field);
        }
        else
        {
            this.customfields=field;
        }
    }


    public Fields(NeoLoadTestContext context, String maxVu, String testoverviewpng)
    {
        setProject(new Project(context.getDescription().getProject()));

        setIssuetype(new IssueType(ISSUE_TYPE));

        if(context.getDescription().getFixVersions().isPresent())
        {
          List<FixVersion> versionList=new ArrayList<>();
          versionList.add(new FixVersion(context.getDescription().getFixVersions().get()));

            setFixVersions(versionList);

        }


        setSummary("Test Execution of "+context.getProjectName()+" of the test "+context.getScenarioName());
        setDescription("Performance testing results, for more information visit [NeoLoad Web|"+context.gettestURL()+"]");
        addStatisticsInDescirption(context.getStatistics(),maxVu,testoverviewpng,context);
        //        if(context.getDescription().getRevision().isPresent())

        if(context.getDescription().getCustomFields()!=null && context.getDescription().getCustomFields().isPresent())
            setCustomfields(context.getDescription().getCustomFields().get());


    }


    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public IssueType getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(IssueType issuetype) {
        this.issuetype = issuetype;
    }

    public List<FixVersion> getFixVersions() {
        return fixVersions;
    }

    public void setFixVersions(List<FixVersion> fixVersions) {
        this.fixVersions = fixVersions;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addDescription(String description)
    {
        this.description=this.description+"\n"+description;
    }

    private void addStatisticsInDescirption(TestStatistics statistics, String maxVu, String testoverviewpng, NeoLoadTestContext context)
    {
        addDescription("!"+testoverviewpng+"|align=center!");
        addDescription("Here are the global statistics of the test :");
        addDescription("* " + USER_LOAD+ ":" + maxVu);
        addDescription("* " + REQUEST_COUNT+ " : "+statistics.getTotalRequestCountPerSecond().toString());
        addDescription("* " + REQUEST_DURATION + ": "+ statistics.getTotalRequestDurationAverage().toString());
        addDescription("* " + TRANSACTION_AVG_DURATION+ " : "+ statistics.getTotalTransactionDurationAverage().toString());
        addDescription("* " + FAILURE_RATE + ":" + statistics.getTotalGlobalCountFailure().toString());
        addDescription("* " + DOWNLOADED_BYTES + " : " +statistics.getTotalGlobalDownloadedBytes().toString());
        addDescription("* " + DOWNLOADED_BYTES_PER_SECONDS +" : "+ statistics.getTotalGlobalDownloadedBytesPerSecond().toString());
    }
}
