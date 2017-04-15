package com.michaelpalmer.stargazer;


public class GalleryImage {

    static final int LIKE = 1, NO_VOTE = 0, DISLIKE = -1;

    private int id, like = NO_VOTE;
    private String url;

    public GalleryImage(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
