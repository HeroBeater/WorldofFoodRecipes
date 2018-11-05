package com.example.alex.worldoffoodrecipes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review {

    private String usernameOfReview;
    private String titleOfReview;
    private String descriptionOfReview;
    private Float rating;
    private String timestamp;

    Review(String usernameOfReview, String titleOfReview, String descriptionOfReview, Float rating) {
        this.usernameOfReview = usernameOfReview;
        this.titleOfReview = titleOfReview;
        this.descriptionOfReview = descriptionOfReview;
        this.rating = rating;
        timestamp = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    String getUsernameOfReview() {
        return usernameOfReview;
    }

    public void setUsernameOfReview(String usernameOfReview) {
        this.usernameOfReview = usernameOfReview;
    }

    public String getTitleOfReview() {
        return titleOfReview;
    }

    public void setTitleOfReview(String titleOfReview) {
        this.titleOfReview = titleOfReview;
    }

    public String getDescriptionOfReview() {
        return descriptionOfReview;
    }

    public void setDescriptionOfReview(String descriptionOfReview) {
        this.descriptionOfReview = descriptionOfReview;
    }

    Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
