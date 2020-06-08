package com.bontech.tools.relay.model;

import com.bontech.tools.relay.model.enums.BodyTypes;

import java.io.Serializable;
import java.util.Objects;

public class AbstractTransferEntity extends BaseEntity implements Serializable {

    protected String contentType;
    protected Object body;
    protected BodyTypes bodyType;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        updateModifiedTimeStamp();
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
        updateModifiedTimeStamp();
    }

    public BodyTypes getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyTypes bodyType) {
        this.bodyType = bodyType;
        updateModifiedTimeStamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractTransferEntity)) return false;
        AbstractTransferEntity that = (AbstractTransferEntity) o;
        return Objects.equals(contentType, that.contentType) &&
                Objects.equals(body, that.body) &&
                bodyType == that.bodyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
