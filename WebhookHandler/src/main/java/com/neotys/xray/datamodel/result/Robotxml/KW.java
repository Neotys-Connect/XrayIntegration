package com.neotys.xray.datamodel.result.Robotxml;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Optional;
@XmlRootElement
public class KW {

    private String name;

    private Optional<MSG> msg;

    private Status status;

    public KW(String name, Optional<MSG> msg, Status status) {
        this.name = name;
        this.msg = msg;
        this.status = status;
    }

    public KW()
    {

    }
    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @XmlElement( required = false)
    public MSG getMsg() {

        if(msg.isPresent())
            return msg.get();
        else
            return null;
    }

    public void setMsg(Optional<MSG> msg) {
        this.msg = msg;
    }
    @XmlElement
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
