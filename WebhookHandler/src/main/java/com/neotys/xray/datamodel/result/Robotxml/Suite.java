package com.neotys.xray.datamodel.result.Robotxml;


import com.neotys.xray.HttpResult.NeoLoadTestContext;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.neotys.xray.conf.Constants.NEOLOAD;
@XmlRootElement
public class Suite {

    private int id;

    private String name;

    private Test test;

   /* public Suite(int id, String name, Test test) {
        this.id = id;
        this.name = name;
        this.test = test;
    }**/

    public Suite()
    {

    }
    public Suite(NeoLoadTestContext context)
    {
        this.id=1;
        this.name=NEOLOAD+context.getProjectName();
        this.test=new Test(context);
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
    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }
}
