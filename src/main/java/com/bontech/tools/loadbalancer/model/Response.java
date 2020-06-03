package com.bontech.tools.loadbalancer.model;

import java.io.Serializable;
import java.util.Objects;

public class Response extends AbstractTransferEntity implements Serializable {

    private int statusCode;
    private boolean active;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        updateModifiedTimeStamp();
    }

    public boolean isActive() {
        return active;
    }

    protected void setActive(boolean active) {
        this.active = active;
        updateModifiedTimeStamp();
    }

    public boolean isEmpty(){
        return statusCode == 0 && body == null && contentType == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Response response = (Response) o;
        return statusCode == response.statusCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), statusCode);
    }
}
