package com.neotys.xray.datamodel.result.Robotxml;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="tag")
public class Tag {

    private String value;

    public Tag(String value) {
        this.value = value;
    }

    public Tag()
    {

    }
    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
