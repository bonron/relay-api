package com.bontech.tools.loadbalancer.utils;

import com.bontech.tools.loadbalancer.model.Request;
import com.bontech.tools.loadbalancer.model.enums.BodyTypes;
import com.bontech.tools.loadbalancer.model.enums.RequestMethods;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Scanner;

@Service
public class Extractor {

    public Request extractRequest(HttpServletRequest request) {
        var request_ = new Request();

        request_.setPath(request.getRequestURI());

        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            var headerName = (String) headerNames.nextElement();
            request_.addHeader(headerName, request.getHeader(headerName));
        }

        var params = request.getParameterNames();
        while (params.hasMoreElements()) {
            var paramName = (String) params.nextElement();
            request_.addParameter(paramName, request.getParameter(paramName));
        }

        var queryString = request.getQueryString();
        if(queryString != null)
        request_.setQueryString(queryString);

        request_.setContentType(request.getContentType());
        extractRequestBody(request, request_);
        return request_;
    }

    static void extractRequestBody(HttpServletRequest request, Request request_) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            request_.setMethod(RequestMethods.POST);
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                e.printStackTrace();
            }
            request_.setBody(s.hasNext() ? s.next() : "");
            request_.setBodyType(BodyTypes.STRING);
            return;
        }
        request_.setMethod(RequestMethods.GET);// TODO fix this to use correct method
    }
}
