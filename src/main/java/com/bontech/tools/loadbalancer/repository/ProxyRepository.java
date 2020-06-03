package com.bontech.tools.loadbalancer.repository;

import com.bontech.tools.loadbalancer.model.Proxy;
import com.bontech.tools.loadbalancer.model.Request;
import com.bontech.tools.loadbalancer.model.Response;
import com.bontech.tools.loadbalancer.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProxyRepository {

    @Autowired
    public Logger logger;
    private Set<Proxy> proxies;
    private static ProxyRepository instance;

    private ProxyRepository() {
        if (instance == null) {
            instance = this;
            proxies = new HashSet<>();
        }
    }

    public synchronized static ProxyRepository getInstance() {
        return instance == null ? new ProxyRepository() : instance;
    }

    public Set<Proxy> getProxies() {
        return proxies;
    }

    public synchronized boolean addProxy(Proxy proxy) {
        var check = proxies.add(proxy);
        if (check) ;//System.out.println(proxy.asFixture());
        if (!check) {
            Proxy internalProxy = null;
            for (Proxy x1 : proxies) {
                if (x1.equals(proxy)) {
                    internalProxy = x1;
                    break;
                }
            }
            if (internalProxy != null) {
                if (internalProxy.getResponses().stream().noneMatch(x -> proxy.getResponses().contains(x)))
                    proxy.getResponses().forEach(internalProxy::addResponse);
            }
        }
        return check;
    }

    public boolean addProxy(Request request, Response response) {
        var proxy = new Proxy(request, response);
        return addProxy(proxy);
    }

    public Set<Proxy> fetchPathMatching(String pathRegex) {
        return pathRegex == null ? new HashSet<>() : proxies.stream().filter(x -> x.getRequest().getPath().matches(pathRegex)).collect(Collectors.toSet());
    }

    public boolean serializeSession(String title, String description) {
        try (var o = new ObjectOutputStream(new FileOutputStream(new File(title + Instant.now().getEpochSecond() + ".brds")))) {
            o.writeUTF(title);
            o.writeUTF(description);
            o.writeObject(proxies);
            proxies = new HashSet<>();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String loadSession(String session) {
        session += session.endsWith(".brds") ? "" : ".brds";
        try (var o = new ObjectInputStream(new FileInputStream(new File(session)))) {
            var title = o.readUTF();
            var description = o.readUTF();
            proxies = (Set<Proxy>) o.readObject();
            return title + "\n" + description;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return "Failed\n" + e.getMessage();
        }
    }

    public Optional<Proxy> findById(Long id) {
        return proxies.stream().filter(x -> x.getId().equals(id)).findFirst();
    }
}
