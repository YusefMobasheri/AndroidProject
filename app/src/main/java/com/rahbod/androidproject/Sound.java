package com.rahbod.androidproject;

public class Sound {
    private Integer id;
    private String user;
    private String voiceUri;
    private String title;

    Sound(String uri, String title, String user){
        setVoiceUri(uri);
        setTitle(title);
        setUser(user);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoiceUri() {
        return voiceUri;
    }

    public void setVoiceUri(String voiceUri) {
        this.voiceUri = voiceUri;
    }
}