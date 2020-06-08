package com.bontech.tools.relay.repository;

import com.bontech.tools.relay.model.Request;

import java.util.HashSet;
import java.util.Set;


public class RequestRepository {

    private Set<Request> requests;

    private static RequestRepository instance;

    private RequestRepository() {
        if (instance == null) {
            instance = this;
            requests = new HashSet<>();
        }
    }

    public synchronized static RequestRepository getInstance() {
        return instance == null ? new RequestRepository() : instance;
    }

    public Set<Request> getRequests() {
        return requests;
    }

    public boolean addRequest(Request request) {
        return requests.add(request);
    }
}
