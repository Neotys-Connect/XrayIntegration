package com.neotys.xray.datamodel.result.Robotxml;

import com.neotys.ascode.swagger.client.ApiException;
import com.neotys.ascode.swagger.client.api.ResultsApi;
import com.neotys.ascode.swagger.client.model.*;
import com.neotys.xray.HttpResult.NeoLoadTestContext;
import com.neotys.xray.Logger.NeoLoadLogger;
import com.neotys.xray.common.NeoLoadUtils;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.neotys.xray.conf.Constants.SLA_TYPE_PERINTERVAL;
import static com.neotys.xray.conf.Constants.SLA_TYPE_PERTEST;
@XmlRootElement
public class Test {

    //[
    //  {
    //    "kpi": "avg-transaction-resp-time",
    //    "status": "PASSED",
    //    "value": 0.3087143,
    //    "warningThreshold": {
    //      "operator": ">=",
    //      "value": 4
    //    },
    //    "failedThreshold": {
    //      "operator": ">=",
    //      "value": 7
    //    },
    //    "element": {
    //      "elementId": "8dbff581-f7d7-4dc1-98f3-b2a77061d82a",
    //      "name": "return to reports page",
    //      "category": "TRANSACTION",
    //      "userpath": "BrowserUser_Create_report",
    //      "parent": "Actions"
    //    }
    //  },
    //  {
    //    "kpi": "avg-transaction-resp-time",
    //    "status": "PASSED",
    //    "value": 1.668,
    //    "warningThreshold": {
    //      "operator": ">=",
    //      "value": 4
    //    },
    //    "failedThreshold": {
    //      "operator": ">=",
    //      "value": 7
    //    },
    //    "element": {
    //      "elementId": "339eb312-2a5a-44af-bb55-48176fc8c2a2",
    //      "name": "submit report",
    //      "category": "TRANSACTION",
    //      "userpath": "BrowserUser_Create_report",
    //      "parent": "Actions"
    //    }
    //  },
    //  {
    //    "kpi": "avg-transaction-resp-time",
    //    "status": "PASSED",
    //    "value": 0.96914285,
    //    "warningThreshold": {
    //      "operator": ">=",
    //      "value": 4
    //    },
    //    "failedThreshold": {
    //      "operator": ">=",
    //      "value": 7
    //    },
    //    "element": {
    //      "elementId": "eb1cee2c-2f37-43f7-a2bd-92cc6990f92f",
    //      "name": "submit",
    //      "category": "TRANSACTION",
    //      "userpath": "BrowserUser_Create_report",
    //      "parent": "Try"
    //    }
    //  },
    //  {
    //    "kpi": "avg-transaction-resp-time",
    //    "status": "PASSED",
    //    "value": 1.5881429,
    //    "warningThreshold": {
    //      "operator": ">=",
    //      "value": 4
    //    },
    //    "failedThreshold": {
    //      "operator": ">=",
    //      "value": 7
    //    },
    //    "element": {
    //      "elementId": "f18b10f8-9f51-44ac-b17d-22eed776ab27",
    //      "name": "Home",
    //      "category": "TRANSACTION",
    //      "userpath": "BrowserUser_Create_report",
    //      "parent": "Actions"
    //    }
    //  }
    //]

    private int id;

    private String name;
    private List<KW> kw;
    private Optional<Status> status;
    private Optional<Tags> tags;

    public Test(int id, String name, List<KW> kw, Optional<Status> status, Optional<Tags> tags) {
        this.id = id;
        this.name = name;
        this.kw = kw;
        this.status = status;
        this.tags = tags;
    }

    public Test()
    {

    }
    public Test(NeoLoadTestContext context, ResultsApi resultsApi)
    {
        NeoLoadLogger loadLogger;

        this.id=2;
        this.name=context.getScenarioName();
        this.kw=new ArrayList<>();
        String startdate= NeoLoadUtils.convertDateLongToString(context.getTeststart());
        String endate=NeoLoadUtils.convertDateLongToString(context.getTestEnd());
        loadLogger=context.getLogger();
        //----transform the NL SLA into KW-------------
        try {
            loadLogger.debug("getting globla SLA");
            if (context.getArrayOfSLAGlobalIndicatorDefinitionOptional().isPresent())
                addGlobalSLAIndicators(startdate, endate, context.getArrayOfSLAGlobalIndicatorDefinitionOptional().get());
            loadLogger.debug("getting SLA per test");
            if (context.getArrayOfSLAPerTestDefinition().isPresent())
                addSLAPerTest(startdate, endate, context.getArrayOfSLAPerTestDefinition().get(), resultsApi, context.getTestid(),context);
            loadLogger.debug("getting SLA per time interval");
            if (context.getArrayOfSLAPerIntervalDefinition().isPresent())
                addSLAPerInterval(startdate, endate, context.getArrayOfSLAPerIntervalDefinition().get(), resultsApi, context.getTestid(), context);

            Status status = new Status(context.getStatus(), startdate, endate, Optional.empty());
            setStatus(Optional.of(status));
            loadLogger.debug("Status of the test is " + status.getContent());
            List<String> taglist = new ArrayList<>();
            taglist.add("neoload");
            taglist.add("performance");

            if (context.getDescription().getTags().isPresent()) {
                taglist.addAll(context.getDescription().getTags().get().stream().collect(Collectors.toList()));
            }
            Tags tags = new Tags();
            tags.setTag(taglist);
            setTags(Optional.ofNullable(tags));
        }
        catch (Exception e) {
            context.getLogger().error("Technical Error " + e.getMessage(),e);
        }

    }

    private void addGlobalSLAIndicators(String start,String end,ArrayOfSLAGlobalIndicatorDefinition arrayOfSLAGlobalIndicatorDefinition)
    {
        arrayOfSLAGlobalIndicatorDefinition.forEach(slaGlobalIndicatorDefinition -> {
            KW indicator=new KW();
            if(slaGlobalIndicatorDefinition.getKpi()!=null)
                indicator.setName("GLOBAL_"+slaGlobalIndicatorDefinition.getKpi().getValue());
            else
                indicator.setName("GLOBAL_");
            indicator.setStatus(new Status(slaGlobalIndicatorDefinition.getStatus().getValue(),start,end, Optional.empty()));
            String level;
            if(slaGlobalIndicatorDefinition.getStatus().getValue().equalsIgnoreCase("FAILED"))
                level="ERROR";
            else
                level="INFO";

            if(slaGlobalIndicatorDefinition.getStatus().getValue().equalsIgnoreCase("FAILED")) {
                if (slaGlobalIndicatorDefinition.getKpi() != null)
                    indicator.setMsg(Optional.of(new MSG(level, slaGlobalIndicatorDefinition.getKpi().getValue() + " equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Failed Thershold is " +  getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold()))));
                else
                    indicator.setMsg(Optional.of(new MSG(level, "global SLA equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Failed Thershold is " +  getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold()))));

            }
            if(slaGlobalIndicatorDefinition.getStatus().getValue().equalsIgnoreCase("WARNING"))
            {
                if (slaGlobalIndicatorDefinition.getKpi() != null)
                    indicator.setMsg(Optional.of(new MSG(level, slaGlobalIndicatorDefinition.getKpi().getValue() + " equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Warning Thershold is " +  getThresholdString(slaGlobalIndicatorDefinition.getWarningThreshold()))));
                else
                    indicator.setMsg(Optional.of(new MSG(level, "global SLA equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Warning Thershold is " +  getThresholdString(slaGlobalIndicatorDefinition.getWarningThreshold()))));

            }
            else
            {
                if (slaGlobalIndicatorDefinition.getKpi() != null)
                    indicator.setMsg(Optional.of(new MSG(level, slaGlobalIndicatorDefinition.getKpi().getValue() + " equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Failed Thershold is " + getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold()))));
                else
                    indicator.setMsg(Optional.of(new MSG(level, "global SLA equal to " + slaGlobalIndicatorDefinition.getValue().toString() + " Failed Thershold is " +  getThresholdString(slaGlobalIndicatorDefinition.getFailedThreshold()))));

            }
                kw.add(indicator);
        });
    }
    @XmlElement(required = false)
    public Status getStatus() {
        if(status.isPresent())
            return status.get();
        else
            return null;
    }



    public void setStatus(Optional<Status> status) {
        this.status = status;
    }

    @XmlElement( name="tags",required = false)
    public Tags getTags() {
        if(tags.isPresent())
            return tags.get();
        else
            return null;
    }

    public void setTags(Optional<Tags> tags) {
        this.tags = tags;
    }

    private String generateKWname(SLAElementDefinition definition, SLAKPIDefinition kpiname, String type)
    {
        String userPathName="";
        if(definition.getUserPath()!=null)
            userPathName=definition.getUserPath();


        if(kpiname!=null)
            return type+"."+userPathName+"."+definition.getName().replaceAll(" ","_")+"."+definition.getCategory().getValue()+"."+kpiname.getValue();
        else
            return type+"."+userPathName+"."+definition.getName().replaceAll(" ","_")+"."+definition.getCategory().getValue();

    }
    private void addSLAPerTest(String start, String end, ArrayOfSLAPerTestDefinition arrayOfSLAPerTestDefinition, ResultsApi resultsApi, String testid, NeoLoadTestContext context)
    {
        context.getLogger().debug("Starting to retrieve SLA per test");
        arrayOfSLAPerTestDefinition.forEach(slaPerTestDefinition -> {
                    context.getLogger().debug("working with sla " + slaPerTestDefinition.toString());

                    KW indicator=new KW();

                    indicator.setName(generateKWname(slaPerTestDefinition.getElement(),slaPerTestDefinition.getKpi(),SLA_TYPE_PERTEST));
                    indicator.setStatus(new Status(slaPerTestDefinition.getStatus().getValue(),start,end, Optional.empty()));
                    String level;
                    if(slaPerTestDefinition.getStatus().getValue().equalsIgnoreCase("FAILED"))
                        level="ERROR";
                    else
                        level="INFO";




                    if(slaPerTestDefinition.getStatus().getValue().equalsIgnoreCase("FAILED"))
                        indicator.setMsg(Optional.ofNullable(generateMessage("FAILED",slaPerTestDefinition.getFailedThreshold(),slaPerTestDefinition.getValue(),slaPerTestDefinition.getElement(),slaPerTestDefinition.getKpi(),level,null)));
                    else
                    {
                        if(slaPerTestDefinition.getStatus().getValue().equalsIgnoreCase("WARNING")) {
                            if(slaPerTestDefinition.getWarningThreshold()!=null)
                                 indicator.setMsg(Optional.ofNullable(generateMessage("WARNING", slaPerTestDefinition.getWarningThreshold(), slaPerTestDefinition.getValue(), slaPerTestDefinition.getElement(), slaPerTestDefinition.getKpi(), level, null)));
                            else
                                indicator.setMsg(Optional.ofNullable(generateMessage("WARNING", slaPerTestDefinition.getFailedThreshold(), slaPerTestDefinition.getValue(), slaPerTestDefinition.getElement(), slaPerTestDefinition.getKpi(), level, null)));
                        }
                        else
                            indicator.setMsg(Optional.ofNullable(generateMessage("FAILED",slaPerTestDefinition.getFailedThreshold(),slaPerTestDefinition.getValue(),slaPerTestDefinition.getElement(),slaPerTestDefinition.getKpi(),level,null)));

                    }
                        kw.add(indicator);
                }

                );
    }

    private MSG generateMessage(String failed, ThresholdDefinition definition, Float value, SLAElementDefinition elementDefinition, SLAKPIDefinition slakpiDefinition, String level, @Nullable Long maxvalue)
    {
        if(maxvalue!=null) {
            if (slakpiDefinition != null)
                return new MSG(level, elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " the kpi :" + slakpiDefinition.getValue() + " equal to " + String.valueOf(maxvalue.doubleValue()) + " - the " + failed + " Threshold is " + getThresholdString(definition));
            else
                return new MSG(level, elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " SLA equal to " + String.valueOf(maxvalue.doubleValue()) + " - the " + failed + " Threshold is " + getThresholdString(definition));
        }
        else
        {
            if (slakpiDefinition != null)
                return new MSG(level, elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " the kpi :" + slakpiDefinition.getValue() + " equal to " + String.valueOf(value) + " - the " + failed + " Threshold is " + getThresholdString(definition));
            else
                return new MSG(level, elementDefinition.getCategory().getValue() + " with the name " + elementDefinition.getName() + " SLA equal to " + String.valueOf(value) + " - the " + failed + " Threshold is " + getThresholdString(definition));

        }
    }

    private String getThresholdString(ThresholdDefinition definition)
    {
        String result;
        result= "value "+definition.getOperator().getValue()+" to " + definition.getValue().toString();

        return result;
    }
    private void    addSLAPerInterval(String start, String end, ArrayOfSLAPerIntervalDefinition arrayOfSLAPerIntervalDefinition, ResultsApi resultsApi, String testid, NeoLoadTestContext context)
    {
        arrayOfSLAPerIntervalDefinition.forEach(slaPerTestDefinition -> {
                    KW indicator=new KW();

                    indicator.setName(generateKWname(slaPerTestDefinition.getElement(),slaPerTestDefinition.getKpi(),SLA_TYPE_PERINTERVAL));
                    indicator.setStatus(new Status(slaPerTestDefinition.getStatus().getValue(),start,end, Optional.empty()));
                    String level;
                    if(slaPerTestDefinition.getStatus().getValue().equalsIgnoreCase("FAILED"))
                       level="ERROR";
                    else
                        level="INFO";

                    Long max= null;
                     if(slaPerTestDefinition.getElement().getElementID()!= null)
                     {
                         //----collect the max value-----

                         try {
                             ElementValues values=resultsApi.getTestElementsValues(testid,slaPerTestDefinition.getElement().getElementID());
                              max=values.getMaxDuration();
                              max=max/1000;
                         } catch (ApiException e) {
                             context.getLogger().error("Technical error :"+e.getMessage(),e);
                         }

                     }
                     if(slaPerTestDefinition.getStatus().getValue().equalsIgnoreCase("FAILED")) {
                         indicator.setMsg(Optional.ofNullable(generateMessage("FAILED", slaPerTestDefinition.getFailedThreshold(), slaPerTestDefinition.getFailed(), slaPerTestDefinition.getElement(), slaPerTestDefinition.getKpi(), level,max)));
                     }
                     if(slaPerTestDefinition.getStatus().getValue().equalsIgnoreCase("WARNING")) {
                         if(slaPerTestDefinition.getWarningThreshold()!=null)
                             indicator.setMsg(Optional.ofNullable(generateMessage("WARNING", slaPerTestDefinition.getWarningThreshold(), slaPerTestDefinition.getWarning(), slaPerTestDefinition.getElement(), slaPerTestDefinition.getKpi(), level, max)));
                         else
                             indicator.setMsg(Optional.ofNullable(generateMessage("WARNING", slaPerTestDefinition.getFailedThreshold(), slaPerTestDefinition.getWarning(), slaPerTestDefinition.getElement(), slaPerTestDefinition.getKpi(), level, max)));

                     }
                     else
                         indicator.setMsg(Optional.ofNullable(generateMessage("FAILED", slaPerTestDefinition.getFailedThreshold(), slaPerTestDefinition.getFailed(), slaPerTestDefinition.getElement(), slaPerTestDefinition.getKpi(), level,max)));

            kw.add(indicator);
                }

        );
    }
    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @XmlElement
    public List<KW> getKw() {
        return kw;
    }


    public void setKw(List<KW> kw) {
        this.kw = kw;
    }
}
