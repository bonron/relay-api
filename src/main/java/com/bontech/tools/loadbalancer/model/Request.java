package com.bontech.tools.loadbalancer.model;

import com.bontech.tools.loadbalancer.model.enums.RequestMethods;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Request extends AbstractTransferEntity implements Serializable {

    private Map<String, String> headers;
    private Map<String, String> parameters;
    private RequestMethods method;
    private String path;
    private String queryString;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
        updateModifiedTimeStamp();
    }

    public void addHeader(String key, String value) {

    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        updateModifiedTimeStamp();
    }

    public void addParameter(String key, String value) {

    }

    public RequestMethods getMethod() {
        return method;
    }

    public void setMethod(RequestMethods method) {
        this.method = method;
        updateModifiedTimeStamp();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        updateModifiedTimeStamp();
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
        updateModifiedTimeStamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Request request = (Request) o;
        return Objects.equals(headers, request.headers) &&
                method == request.method &&
                path.equals(request.path) &&
                Objects.equals(queryString, request.queryString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), method, path, queryString);
    }

    @Override
    public String toString() {
        return "\tRequest{" +
                "\n\t\tparameters=" + parameters +
                ",\n\t\tmethod=" + method +
                ",\n\t\tbody='" + body + '\'' +
                ",\n\t\tbodyType='" + bodyType + '\'' +
                ",\n\t\tpath='" + path + (queryString == null ? '\'' : '?' + queryString + '\'') +
                "\n\t}";
    }
}
