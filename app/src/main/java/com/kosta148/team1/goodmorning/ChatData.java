package com.kosta148.team1.goodmorning;

public class ChatData {
    private String id;
    private String text;
    private String date;
    // 빈 생성자가 없으면 getValue() 시 에러 발생
    public ChatData() {

    }

    // 생성자
    public ChatData(String id, String text, String date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
