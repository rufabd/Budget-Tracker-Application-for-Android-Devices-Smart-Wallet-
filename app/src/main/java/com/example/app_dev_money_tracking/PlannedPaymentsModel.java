package com.example.app_dev_money_tracking;

public class PlannedPaymentsModel {
    private int id;
    private String note;
    private int amount;
    private int categoryId;
    private String status;
    private String currency;
    private RecordTypeModel.RecordTypeKey recordType;
    private String date;


    public PlannedPaymentsModel(int id, String note, int amount, int categoryId, String status, String currency, RecordTypeModel.RecordTypeKey recordType, String date) {
        this.id = id;
        this.note = note;
        this.amount = amount;
        this.categoryId = categoryId;
        this.status = status;
        this.currency = currency;
        this.recordType = recordType;
        this.date = date;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public RecordTypeModel.RecordTypeKey getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordTypeModel.RecordTypeKey recordType) {
        this.recordType = recordType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
