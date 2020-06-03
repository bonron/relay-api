package com.bontech.tools.loadbalancer.controller;

import com.bontech.tools.loadbalancer.model.enums.BodyTypes;
import com.bontech.tools.loadbalancer.repository.RequestRepository;
import com.bontech.tools.loadbalancer.utils.Extractor;
import com.bontech.tools.loadbalancer.utils.Logger;
import com.bontech.tools.loadbalancer.utils.Sender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/**")
public class RelayController {

    final Extractor extractor;

    final Logger logger;

    final Sender sender;

    final RequestRepository requestRepository;

    public RelayController(Extractor extractor, Logger logger, Sender sender) {
        this.extractor = extractor;
        this.logger = logger;
        this.sender = sender;
        this.requestRepository = RequestRepository.getInstance();
    }

    @RequestMapping(method = RequestMethod.GET)
    public void get(HttpServletRequest request, HttpServletResponse response) {
        extractResponse(request, response);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void post(HttpServletRequest request, HttpServletResponse response) {
        extractResponse(request, response);
    }

    private void extractResponse(HttpServletRequest request, HttpServletResponse response) {
        var request_ = extractor.extractRequest(request);
        requestRepository.addRequest(request_);
        var response_ = sender.send(request_);
        response.setStatus(response_.getStatusCode());
        try {
            if (response_.getBodyType() == BodyTypes.BINARY)
                response.getOutputStream().write((byte[])response_.getBody());
            else response.getOutputStream().write(((String)response_.getBody()).getBytes());
            response.setContentType(response_.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
