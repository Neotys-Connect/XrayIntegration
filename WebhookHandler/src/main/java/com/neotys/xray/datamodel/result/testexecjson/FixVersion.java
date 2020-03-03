package com.neotys.xray.datamodel.result.testexecjson;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FixVersion {
    private String name;

    public FixVersion(String s) {
        name=s;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
}
