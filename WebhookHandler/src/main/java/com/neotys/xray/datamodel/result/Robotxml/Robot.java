package com.neotys.xray.datamodel.result.Robotxml;

import com.neotys.xray.HttpResult.NeoLoadTestContext;
import com.neotys.xray.common.NeoLoadUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Robot {
    @XmlAttribute
    private static final String generator="NeoLoad";

    private String generated;

    private static boolean rpa=false;

    private Suite suite;


    public Robot() {

    }

    public Robot(NeoLoadTestContext context) {
        this.generated = NeoLoadUtils.convertDateLongToString(context.getTestEnd());
        this.suite = new Suite(context);
    }

    public static String getGenerator() {
        return generator;
    }
    @XmlAttribute
    public String getGenerated() {
        return generated;
    }

    public void setGenerated(String generated) {
        this.generated = generated;
    }
    @XmlAttribute
    public  boolean isRpa() {
        return rpa;
    }

    public static void setRpa(boolean rpa) {
        Robot.rpa = rpa;
    }
    @XmlElement
    public Suite getSuite() {
        return suite;
    }

    public void setSuite(Suite suite) {
        this.suite = suite;
    }
}
