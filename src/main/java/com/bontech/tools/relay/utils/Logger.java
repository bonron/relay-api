package com.bontech.tools.relay.utils;


import com.bontech.tools.relay.model.Proxy;
import com.bontech.tools.relay.model.Request;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class Logger {

    private static int failedRequests;
    private static int successRequests;

    public void log(String... logs){
        for (String log : logs) {
            System.out.println(log);
        }
    }

    public void log(Exception exception){
        System.out.println("Exception = " + exception);
    }

    public synchronized void logRequest(Request request){
        System.out.println("Success Count: " + ++successRequests);
        System.out.println(request);
    }

    public synchronized void logFailedRequest(Request request, String... info){
        System.out.println("Failed Count: " + ++failedRequests);
        System.out.println(request);
        Arrays.stream(info).forEach(System.out::println);
    }

    public void logProxy(Proxy proxy){

    }
}
