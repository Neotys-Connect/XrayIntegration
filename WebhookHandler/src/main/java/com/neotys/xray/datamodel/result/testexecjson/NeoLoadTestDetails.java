package com.neotys.xray.datamodel.result.testexecjson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neotys.xray.HttpResult.NeoLoadTestContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NeoLoadTestDetails {

    Fields fields;
    XRayFields xrayFields;

    public NeoLoadTestDetails(NeoLoadTestContext context, boolean iscloud, String maxVu,   String testoverviewpng)
    {
        fields=new Fields(context,maxVu,testoverviewpng);
        if(iscloud)
        {
           if(context.getDescription().getTestEnvironment().isPresent())
           {
               List<String> env=new ArrayList<>();
               env.add(context.getDescription().getTestEnvironment().get());
               xrayFields=new XRayFields(context.getDescription().getTestPlan().get(),env);
           }
           else
           {
               xrayFields=new XRayFields(context.getDescription().getTestPlan().get(),null);
           }
        }
        else {
            xrayFields = null;
        }
     }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public XRayFields getXrayFields() {
        return xrayFields;
    }

    public void setXrayFields(XRayFields xrayFields) {
        this.xrayFields = xrayFields;
    }
}
