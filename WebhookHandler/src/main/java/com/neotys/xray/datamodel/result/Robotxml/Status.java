package com.neotys.xray.datamodel.result.Robotxml;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.Optional;

import static com.neotys.xray.conf.Constants.JIRA_FAIL_STATUS;
import static com.neotys.xray.conf.Constants.JIRA_PASS_STATUS;
import static com.neotys.xray.conf.Constants.NEOLOAD_PASS_STATUS;

@XmlRootElement
public class Status {



    private String status;


    private String starttime;


    String endtime;
    Optional<String> content;

    public Status(String status, String starttime, String endtime, Optional<String> content) {
        if(status.equalsIgnoreCase(NEOLOAD_PASS_STATUS))
            this.status = JIRA_PASS_STATUS;
        else
            this.status = JIRA_FAIL_STATUS;
        this.starttime = starttime;
        this.endtime = endtime;
        this.content = content;
    }

    public Status()
    {

    }
    @XmlAttribute
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @XmlAttribute
    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }
    @XmlAttribute
    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    @XmlValue
    public String getContent() {
        if(content.isPresent())
            return content.get();
        else
            return "";
    }

    public void setContent(Optional<String> content) {
        this.content = content;
    }
}
