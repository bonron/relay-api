package com.bontech.tools.relay.controller;

import com.bontech.tools.relay.model.Proxy;
import com.bontech.tools.relay.model.Response;
import com.bontech.tools.relay.repository.ProxyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/admin/bonron")
public class AdminController {

    @Autowired
    ProxyRepository proxyRepository;

    @ResponseBody
    @RequestMapping("session/save/{title}/{description}")
    public Boolean serializeSession(@PathVariable String title, @PathVariable String description) {
        return proxyRepository.serializeSession(title, description);
    }

    @ResponseBody
    @RequestMapping(value = "session/load/{session}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String loadSession(@PathVariable String session) {
        var text = proxyRepository.loadSession(session).split("\n");
        return text.length > 1 ? "{\n\t\"title\":\"" + text[0] + "\",\n\t\"description\":\"" + text[1] + "\"\n}" : "info:\"" + text[0] + "\"";
    }

    @ResponseBody
    @RequestMapping({"session", "session/{regex}"})
    public Set<Proxy> viewSession(@PathVariable(required = false) String regex) {
        return proxyRepository.fetchPathMatching(regex == null ? ".*" : regex);
    }

    @ResponseBody
    @RequestMapping(value = "session/proxy/{id}/fixture")
    public String getFixture(@PathVariable Long id) {
        return proxyRepository.findById(id).map(Proxy::asFixture).orElse("");
    }

    @ResponseBody
    @RequestMapping("session/proxy/{id}")
    public Proxy findProxyById(@PathVariable Long id){
        return proxyRepository.findById(id).orElseThrow();
    }

    @ResponseBody
    @RequestMapping("session/proxy/{proxyId}/responses/{responseId}")
    public Proxy setActiveResponse(@PathVariable Long proxyId, @PathVariable Long responseId){
        var proxy = proxyRepository.findById(proxyId).orElseThrow();
        var response = proxy.getResponses().stream().filter(x->x.getId().equals(responseId)).findFirst().orElseThrow();
        proxy.setActiveResponse(response);
        return proxy;
    }

    @ResponseBody
    @RequestMapping(value = "session/proxy/{id}/responses", method = RequestMethod.POST)
    public Proxy addResponse(@PathVariable Long id, @RequestBody Response response){
        var proxy = proxyRepository.findById(id).orElseThrow();
        proxy.addResponse(response);
        return setActiveResponse(id, response.getId());
    }
}
