package com.rahbod.androidproject;

class Sound {
    private Integer id;
    private String user;
    private byte[] voiceStream;
    private String title;

    Sound(Integer id, byte[] uri, String title, String user) {
        setId(id);
        setVoiceUri(uri);
        setTitle(title);
        setUser(user);
    }

    String getUser() {
        return user;
    }

    void setUser(String user) {
        this.user = user;
    }

    void setId(Integer id) {
        this.id = id;
    }

    Integer getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    byte[] getVoiceUri() {
        return voiceStream;
    }

    void setVoiceUri(byte[] voiceStream) {
        this.voiceStream = voiceStream;
    }
}