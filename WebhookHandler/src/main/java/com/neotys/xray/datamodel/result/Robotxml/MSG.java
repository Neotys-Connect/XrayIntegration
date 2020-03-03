package com.neotys.xray.datamodel.result.Robotxml;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
@XmlRootElement
public class MSG {

    private String level;
    private String content;

    public MSG()
    {

    }

    public MSG(String level, String content) {
        this.level = level;
        this.content = content;
    }
    @XmlAttribute
    public String getLevel() {
        return level;
    }


    public void setLevel(String level) {
        this.level = level;
    }

    @XmlValue
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
