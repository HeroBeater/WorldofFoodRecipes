package com.example.alex.worldoffoodrecipes;

import java.util.ArrayList;

public class Recipe {
    private String Name;
    private String Summary;
    private String Description;
    private Double Average_rating;
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> videos = new ArrayList<>();

    public Recipe(){

    }

    public Recipe(String name, String summary, String description, Double average_rating) {
        Name = name;
        Summary = summary;
        Description = description;
        Average_rating = average_rating;
    }

    public Recipe(String name, String summary, String description, ArrayList<String> images) {
        Name = name;
        Summary = summary;
        Description = description;
        this.Average_rating = 0.0;
        this.images = images;
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

    public Double getRating_average() {
        return Average_rating;
    }

    public void setRating_average(Double rating_average) {
        this.Average_rating = rating_average;
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
