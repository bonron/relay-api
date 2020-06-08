package com.bontech.tools.relay.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private static long counter;
    private Long id;
    private LocalDateTime entryTimestamp;
    private LocalDateTime modifiedTimestamp;

    public BaseEntity() {
        entryTimestamp = LocalDateTime.now();
        modifiedTimestamp = entryTimestamp;
        id = ++counter;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getEntryTimestamp() {
        return entryTimestamp;
    }

    public LocalDateTime getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    protected void updateModifiedTimeStamp() {
        modifiedTimestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
