package com.example.alex.worldoffoodrecipes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review {

    private String recipe_ID;
    private String recipe_user;
    private String usernameOfReview;
    private String titleOfReview;
    private String descriptionOfReview;
    private Float rating;
    private String timestamp;

    Review(String recipe, String recipe_user, String usernameOfReview, String titleOfReview, String descriptionOfReview, Float rating) {
        this.recipe_ID = recipe;
        this.recipe_user = recipe_user;
        this.usernameOfReview = usernameOfReview;
        this.titleOfReview = titleOfReview;
        this.descriptionOfReview = descriptionOfReview;
        this.rating = rating;
        timestamp = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public String getRecipe_ID() {
        return recipe_ID;
    }

    public void setRecipe_ID(String recipe_ID) {
        this.recipe_ID = recipe_ID;
    }

    public String getRecipe_user() {
        return recipe_user;
    }

    public void setRecipe_user(String recipe_user) {
        this.recipe_user = recipe_user;
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
