package com.example.teachermanagement.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ManagementRecord {
    private Long id;
    private Map<String, Object> fields = new LinkedHashMap<>();

    public ManagementRecord() {
    }

    public ManagementRecord(Long id, Map<String, Object> fields) {
        this.id = id;
        this.fields = fields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
}

