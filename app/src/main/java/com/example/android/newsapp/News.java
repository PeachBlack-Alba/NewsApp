package com.example.android.newsapp;

public class News {
    private String mName;
    private String sectionName;
    private String webTitle;
    private String webPublicationDate;
    private String webUrl;
    private String mUrlToImage;
    private String contributor;

    public News(String mName, String sectionName, String webTitle, String webPublicationDate, String webUrl, String mUrlToImage, String contributor) {
        this.mName = mName;
        this.sectionName = sectionName;
        this.webTitle = webTitle;
        this.webPublicationDate = webPublicationDate;
        this.webUrl = webUrl;
        this.mUrlToImage = mUrlToImage;
        this.contributor = contributor;
    }


    public String getSectionName() {
        return sectionName;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getmUrlToImage() {
        return mUrlToImage;
    }
}
