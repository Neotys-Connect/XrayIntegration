package com.neotys.xray.datamodel.result.testexecjson;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Optional;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XRayFields {

    String testPlanKey;
    List<String> environments;

    public XRayFields(String testPlanKey, List<String> environments) {
        this.testPlanKey = testPlanKey;
        this.environments = environments;
    }

    public String getTestPlanKey() {
        return testPlanKey;
    }

    public void setTestPlanKey(String testPlanKey) {
        this.testPlanKey = testPlanKey;
    }

    public List<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }
}
