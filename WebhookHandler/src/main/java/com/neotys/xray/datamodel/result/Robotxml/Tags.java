package com.neotys.xray.datamodel.result.Robotxml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="tags")
public class Tags {

    List<String> tag;

    public Tags()
    {

    }


    @XmlElement(name="tag")
    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }
}
