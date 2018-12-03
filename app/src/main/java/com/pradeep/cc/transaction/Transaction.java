package com.pradeep.cc.transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Transaction {
    private final String id;
    private final String smsTimeStamp;
    private String rawData;
    private double amount;
    private String bankName;
    private String ccDetail;
    private String place;
    private double availableBalance;
    private double currentOutStanding;
    private String timeStamp;
    private Map<String,Object> extraInformation = new HashMap<>();;

    public Transaction(String id, String smsTimeStamp, String rawData) {
        this.id = id;
        this.smsTimeStamp = smsTimeStamp;
        this.rawData = rawData;
    }

    public Transaction(String id, double amount, String bankName, String ccDetail, String timeStamp, String place,
                       String smsTimeStamp, double availableBalance, double currentOutStanding) {
        this.id = id;
        this.amount = amount;
        this.bankName = bankName;
        this.ccDetail = ccDetail;
        this.timeStamp = timeStamp;
        this.place = place;
        this.smsTimeStamp = smsTimeStamp;
        this.availableBalance = availableBalance;
        this.currentOutStanding = currentOutStanding;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setCcDetail(String ccDetail) {
        this.ccDetail = ccDetail;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void setCurrentOutStanding(double currentOutStanding) {
        this.currentOutStanding = currentOutStanding;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setExtraInformation(Map<String, Object> extraInformation) {
        this.extraInformation = extraInformation;
    }

    public double getAmount() {
        return amount;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCcDetail() {
        return ccDetail;
    }

    public String getPlace() {
        return place;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getCurrentOutStanding() {
        return currentOutStanding;
    }

    public String getId() {
        return id;
    }

    public String getSmsTimeStamp() {
        return smsTimeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void putExtraInformation(String key, Object value){
        extraInformation.put(key,value);
    }

    public Object getExtraInformation(String key){
        return extraInformation.get(key);
    }

    public Set<String> getExtraInformationKey(){
        return extraInformation.keySet();
    }

    public String getRawData() {
        return rawData;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", bankName='" + bankName + '\'' +
                ", ccDetail='" + ccDetail + '\'' +
                ", place='" + place + '\'' +
                ", smsTimeStamp='" + smsTimeStamp + '\'' +
                ", availableBalance=" + availableBalance +
                ", currentOutStanding=" + currentOutStanding +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
