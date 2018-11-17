package com.example.alex.worldoffoodrecipes;

import java.util.ArrayList;

public class Recipe {
    private String ID;
    private String Name;
    private String KeyWords;
    private String Description;
    private String Author;
    private Double Average_rating;
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> videos = new ArrayList<>();

    Recipe(String name, String keyWords, String description, Double average_rating) {
        Name = name;
        KeyWords = keyWords;
        Description = description;
        Average_rating = average_rating;
    }

    Recipe(String user, String id, String name, String keyWords, String description, Double average_rating) {
        Author = user;
        ID =id;
        Name = name;
        KeyWords = keyWords;
        Description = description;
        Average_rating = average_rating;
    }

    String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Double getAverage_rating() {
        return Average_rating;
    }

    public void setAverage_rating(Double average_rating) {
        Average_rating = average_rating;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getKeyWords() {
        return KeyWords;
    }

    public void setKeyWords(String KeyWords) {
        KeyWords = KeyWords;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    Double getRating_average() {
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
