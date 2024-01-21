package com.socketio.bean;

public class ImagePart {
    private String name; // section name
    private String data; // section data
    private int seq; // section sequence
    private int length; // section length

    public ImagePart(String name, String data, int seq, int length) {
        this.name = name;
        this.data = data;
        this.seq = seq;
        this.length = length;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

}
