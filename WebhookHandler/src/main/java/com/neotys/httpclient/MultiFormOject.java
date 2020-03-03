package com.neotys.httpclient;

public class MultiFormOject {
    String parameterName;
    String filename;
    String path;
    String contentType;

    public MultiFormOject(String parameterName, String filename, String path, String contentType) {
        this.parameterName = parameterName;
        this.filename = filename;
        this.path = path;
        this.contentType = contentType;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
