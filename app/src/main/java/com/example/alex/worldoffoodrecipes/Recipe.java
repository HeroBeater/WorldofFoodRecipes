package com.example.alex.worldoffoodrecipes;

import java.util.ArrayList;

public class Recipe {
    private String Name;
    private String Summary;
    private String Description;
    private ArrayList<Review> review_list = new ArrayList<>();
    private Double rating_average;
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> videos = new ArrayList<>();

    public Recipe(){

    }

    public Recipe(String name, String summary, String description) {
        Name = name;
        Summary = summary;
        Description = description;
    }

    public Recipe(String name, String summary, String description, ArrayList<Review> review_list,
                  Double rating_average, ArrayList<String> images, ArrayList<String> videos) {
        Name = name;
        Summary = summary;
        Description = description;
        this.review_list = review_list;
        this.rating_average = rating_average;
        this.images = images;
        this.videos = videos;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public ArrayList<Review> getReview_list() {
        return review_list;
    }

    public void setReview_list(ArrayList<Review> review_list) {
        this.review_list = review_list;
    }

    public Double getRating_average() {
        return rating_average;
    }

    public void setRating_average(Double rating_average) {
        this.rating_average = rating_average;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }
}
