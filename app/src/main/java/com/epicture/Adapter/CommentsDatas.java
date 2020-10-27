package com.epicture.Adapter;

public class CommentsDatas {
    private String username;
    private String content;
    public String ups;
    public String down;
    public String vote;
    public String id;

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }
}
