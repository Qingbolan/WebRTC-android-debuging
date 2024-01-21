package com.socketio.bean;

public class JoinInfo {
    public String user_name; // user name
    public String group_name; // group name

    public JoinInfo(String user_name, String group_name) {
        this.user_name = user_name;
        this.group_name = group_name;
    }

    public void setUser_name(String user_name) {
    	this.user_name = user_name;
    }
    
    public String getUser_name() {
    	return this.user_name;
    }

    public void setGroup_name(String group_name) {
    	this.group_name = group_name;
    }
    
    public String getGroup_name() {
    	return this.group_name;
    }

}
