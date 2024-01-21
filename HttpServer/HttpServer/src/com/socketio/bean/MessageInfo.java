package com.socketio.bean;

public class MessageInfo {
    public String from; // message sender
    public String to; // message receiver
    public String content; // message content

    public MessageInfo(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
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

    public void setContent(String content) {
    	this.content = content;
    }
    
    public String getContent() {
    	return this.content;
    }
    
}
