package com.bontech.tools.loadbalancer.model;

import com.bontech.tools.loadbalancer.model.enums.BodyTypes;

import java.io.Serializable;
import java.util.*;

public class Proxy extends BaseEntity implements Serializable {

    private Request request;
    private Set<Response> responses;
    private boolean allowOverride = true;

    public Proxy(Request request, Response response) {
        super();
        this.request = request;
        responses = new HashSet<>();
        responses.add(response);
    }

    public Request getRequest() {
        return request;
    }

    public Set<Response> getResponses() {
        return responses;
    }

    public void addResponse(Response response) {
        responses.add(response);
        updateModifiedTimeStamp();
    }

    public boolean allowOverride() {
        return allowOverride && responses.size() > 1 && hasActiveResponse();
    }

    public void allowOverride(boolean allowOverride) {
        this.allowOverride = allowOverride;
        updateModifiedTimeStamp();
    }

    public void setActiveResponse(Response response) {
        if (responses.contains(response)) {
            responses.forEach(x -> x.setActive(false));
            responses.stream().filter(x -> x.equals(response)).limit(1).forEach(x -> x.setActive(true));
            allowOverride(true);
        }
    }

    public String asFixture() {
        if (hasActiveResponse()) {
            var activeResponse = responses.stream().filter(Response::isActive).findFirst().orElse(responses.stream().findFirst().get());
            return activeResponse.getBodyType().equals(BodyTypes.STRING) ? "\nconst host = '<<< API NAME >>>';\n" +
                    "const path = '" + request.getPath() + "';\n" +
                    getQueryStringParametersConst() +
                    "const method = '" + request.getMethod() + "';\n" +
                    "const status = " + activeResponse.getStatusCode() + ";\n" +
                    getRequestBodyConst() +
                    getBodyConst() +
                    getExports():
                    "Non String body types are not supported for fixtures";
        } else
            return "Active response not set";
    }

    public boolean hasActiveResponse() {
        return responses.size() == 1 || responses.stream().anyMatch(Response::isActive);
    }

    public Response getActiveResponse() {
        return responses.stream().filter(Response::isActive).findFirst().orElse(responses.stream().findFirst().get());
    }

    private String getQueryStringParametersConst() {
        return "const queryStringParameters = " + (request.getQueryString() == null ? "{}" : jsonifyQueryString(request.getQueryString())) + ";\n";
    }

    private String getRequestBodyConst() {
        return hasRequestBody() ? "const requestBody = " + sanitizeBody(String.valueOf(request.getBody())) + ";\n\n" : "\n";
    }

    private String getBodyConst() {
        return "const body = " + sanitizeBody(String.valueOf(getActiveResponse().getBody())) + ";\n";
    }

    private String getExports(){
        return String.format("\nmodule.exports = {\n\thost,\n\tpath,\n\tqueryStringParameters,\n\tmethod,\n\tstatus,\n%s\tbody,\n};\n", hasRequestBody() ? "\trequestBody,\n" : "");
    }

    private boolean hasRequestBody(){
        return request.getBodyType() == BodyTypes.STRING;
    }

    private String jsonifyQueryString(String queryString) {
        var pairs = Arrays.stream(queryString.replaceAll("%2C",",").split("&")).reduce(
                new HashMap<String, String>(),
                (x, y) -> {
                    x.put(y.split("=")[0], y.split("=")[1]);
                    return x;
                },
                (i, j) -> {
                    i.putAll(j);
                    return i;
                });
        var pairsText = new StringBuilder();
        pairs.forEach((x, y) -> pairsText.append("\n\"" + x + "\": [\"" + y + "\"],\n"));
        return  sanitizeBody("{" + pairsText.toString() + "}");

    }

    private static String sanitizeBody(String json) {
        var separated = json.replaceAll("'", "\\\\'").
                replaceAll("\"\\s*:", ":").
                replaceAll("\\{\\s*\"", "{\n\"").
                replaceAll("\"\\s*}", "\",\n}").
                replaceAll("0\\s*}", "0,\n}").
                replaceAll("1\\s*}", "1,\n}").
                replaceAll("2\\s*}", "2,\n}").
                replaceAll("3\\s*}", "3,\n}").
                replaceAll("4\\s*}", "4,\n}").
                replaceAll("5\\s*}", "5,\n}").
                replaceAll("6\\s*}", "6,\n}").
                replaceAll("7\\s*}", "7,\n}").
                replaceAll("8\\s*}", "8,\n}").
                replaceAll("9\\s*}", "9,\n}").
                replaceAll("true\\s*}", "true,\n}").
                replaceAll("false\\s*}", "false,\n}").
                replaceAll("null\\s*}", "null,\n}").
                replaceAll("\",\\s*\"", "\",\n\"").
                replaceAll("0,\\s*\"", "0,\n\"").
                replaceAll("1,\\s*\"", "1,\n\"").
                replaceAll("2,\\s*\"", "2,\n\"").
                replaceAll("3,\\s*\"", "3,\n\"").
                replaceAll("4,\\s*\"", "4,\n\"").
                replaceAll("5,\\s*\"", "5,\n\"").
                replaceAll("6,\\s*\"", "6,\n\"").
                replaceAll("7,\\s*\"", "7,\n\"").
                replaceAll("8,\\s*\"", "8,\n\"").
                replaceAll("9,\\s*\"", "9,\n\"").
                replaceAll("},\\s*\"", "},\n\"").
                replaceAll("],\\s*\"", "],\n\"").
                replaceAll("false,\\s*\"", "false,\n\"").
                replaceAll("true,\\s*\"", "true,\n\"").
                replaceAll("null,\\s*\"", "null,\n\"").
                replaceAll("\\[\\s*\\{", "[\n{").
                replaceAll("},\\s*\\{", "},\n{").
                replaceAll("}\\s*}", "},\n}").
                replaceAll("}\\s*]", "},\n]").
                replaceAll("]\\s*}", "],\n}").
                replaceAll("]\\s*]", "],\n]").
                replaceAll("\\n\"", "\n").
                replaceAll("\"\\n", "\",\n").
                replaceAll("]\\n", "],\n").
                replaceAll("}\\n", "},\n").
                replaceAll("0\\n", "0,\n").
                replaceAll("1\\n", "1,\n").
                replaceAll("2\\n", "2,\n").
                replaceAll("3\\n", "3,\n").
                replaceAll("4\\n", "4,\n").
                replaceAll("5\\n", "5,\n").
                replaceAll("6\\n", "6,\n").
                replaceAll("7\\n", "7,\n").
                replaceAll("8\\n", "8,\n").
                replaceAll("9\\n", "9,\n").
                replaceAll("true\\n", "true,\n").
                replaceAll("false\\n", "false,\n").
                replaceAll("null\\n", "null,\n").
                replaceAll("\"", "'").
                split("\\n");
        var formatted = new StringBuilder();
        for (int i = 0, j = 0; i < separated.length; i++) {
            if (separated[i].matches("[^]}\\[\\{]*[]}][^]}]*")) --j;
            formatted.append("\t".repeat(Math.max(0, j)));
            if (separated[i].matches("[^\\[\\{]*[\\[\\{][^\\[\\{}\\]]*")) ++j;
            formatted.append(separated[i]);
            if (i < separated.length - 1) formatted.append('\n');
        }
        return formatted.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proxy proxy = (Proxy) o;
        return request.equals(proxy.request);
    }

    @Override
    public int hashCode() {
        return request.hashCode();
    }
}
