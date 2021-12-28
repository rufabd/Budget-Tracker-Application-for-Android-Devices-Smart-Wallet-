package com.example.app_dev_money_tracking;

public class RecordTypeModel {
    public enum RecordTypeKey{I,E,A};
    private RecordTypeKey id;
    private String recordType;

    public RecordTypeModel(RecordTypeKey id, String recordType) {
        this.id = id;
        this.recordType = recordType;
    }

    public RecordTypeKey getId() {
        return id;
    }

    public void setId(RecordTypeKey id) {
        this.id = id;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
}
