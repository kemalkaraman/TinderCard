package com.example.kemal.testwork;

public class cards {
    private String ImageUrl;
    private String UserId;
    private String User;

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getId() {
        return UserId;
    }

    public void setId(String UserId) {
        UserId = UserId;
    }


    public cards(String ImageUrl, String UserId, String User) {
        this.ImageUrl = ImageUrl;
        this.UserId = UserId;
        this.User = User;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }
}
