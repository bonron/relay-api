package com.bontech.tools.relay.utils;

import com.bontech.tools.relay.model.Proxy;
import com.bontech.tools.relay.model.Request;
import com.bontech.tools.relay.model.Response;
import com.bontech.tools.relay.model.enums.BodyTypes;
import com.bontech.tools.relay.model.enums.RequestMethods;
import com.bontech.tools.relay.repository.ProxyRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

@Service
public class Sender {

    private final Logger logger;

    private final ProxyRepository proxyRepository;

    private String[] routes;

    public Sender(Logger logger, Environment environment) {
        this.logger = logger;
        proxyRepository = ProxyRepository.getInstance();
        routes = environment.getProperty("relay.routes").split(",");
    }

    public Response send(Request request) {
        var response = new Response();
        if (proxyRepository.getProxies().stream().anyMatch(x -> x.getRequest().equals(request)) && proxyRepository.getProxies().stream().anyMatch(x -> x.allowOverride() && x.getResponses().stream().allMatch(y -> y.getBodyType() == BodyTypes.STRING))) {
            var ret = proxyRepository.getProxies().stream().filter(x -> x.getRequest().equals(request)).findFirst().map(Proxy::getActiveResponse).get();
            if (ret.getBodyType() == BodyTypes.STRING)
                return ret;
        }
        for (var env : routes) {
            try {
                var url = new URL(env + request.getPath() +/* encodeParameters(request) +*/ encodeQueryString(request));
                var con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod(request.getMethod().toString());
                con.setRequestProperty("User-Agent", "Bontech Pro's");
                if (request.getContentType() != null) con.setRequestProperty("Content-Type", request.getContentType());
                if (request.getMethod() == RequestMethods.POST) {
                    con.setDoOutput(true);
                    var os = con.getOutputStream();
                    os.write(request.getBodyType() == BodyTypes.BINARY ? (byte[]) request.getBody() : ((String) request.getBody()).getBytes());
                    os.flush();
                    os.close();
                }
                response.setStatusCode(con.getResponseCode());

                if (response.getStatusCode() == HTTP_OK) {
                    response.setContentType(/*request.getPath().contains("/instrument/currencies/GBP") && con.getContentType().equalsIgnoreCase("application/json")?con.getContentType()+"; charset=UTF-8":*/con.getContentType());
                    if (response.getContentType().contains("application/json")) {
                        var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        var buffer = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            buffer.append(inputLine);
                        }
                        in.close();
                        response.setBodyType(BodyTypes.STRING);
                        response.setBody(buffer.toString());
                    } else {
                        response.setBodyType(BodyTypes.BINARY);
                        response.setBody(con.getInputStream().readAllBytes());
                    }
                    logger.log(request.getPath(), String.valueOf(response.getStatusCode()), env);
                    proxyRepository.addProxy(request, response);
                    return response;
                } else {
                    response.setBodyType(BodyTypes.STRING);
                    response.setBody(con.getResponseMessage());
                    logger.logFailedRequest(request, con.getResponseMessage(), String.valueOf(response.getStatusCode()), "url=" + url.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(e, env);
            }
        }
        return response;
    }

    /*private String encodeParameters(Request request) {
        return request.getParameters() == null ? "" : request.getParameters().entrySet().stream().map(x -> x.getKey() + "/" + x.getValue()).reduce("?", (x, y) -> x + "/" + y);
    }*/

    private String encodeQueryString(Request request) {
        return request.getQueryString() == null ? "" : '?' + request.getQueryString();
    }

}
