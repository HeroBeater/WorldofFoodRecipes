package com.example.alex.worldoffoodrecipes;

import java.util.ArrayList;

public class Recipe {
    private String Name;
    private String Summary;
    private String Description;
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

    public Recipe(String name, String summary, String description, Double rating_average, ArrayList<String> images) {
        Name = name;
        Summary = summary;
        Description = description;
        this.rating_average = rating_average;
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
