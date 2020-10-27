package com.epicture.Adapter;

import android.app.Application;

public class GalleryDatasAdapter extends Application {
    private String id;
    private String galleryId;
    private String url;
    private String views;
    private String vote;
    private String title;
    private String description;

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public void setGalleryId(String galleryId) {
        this.galleryId = galleryId;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getViews() {
        return views;
    }

    public String getGalleryId() {
        return galleryId;
    }

    public String getVote() {
        return vote;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
