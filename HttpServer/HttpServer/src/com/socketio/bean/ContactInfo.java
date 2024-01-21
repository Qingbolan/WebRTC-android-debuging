package com.socketio.bean;

public class ContactInfo {
    public String from; // contact source
    public String to; // contact destination

    public ContactInfo(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public void setFrom(String from) {
    	this.from = from;
    }
    
    public String getFrom() {
    	return this.from;
    }

    public void setTo(String to) {
    	this.to = to;
    }
    
    public String getTo() {
    	return this.to;
    }

}
