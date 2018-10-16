package com.example.alex.worldoffoodrecipes;

public class Recipe {
    private String Name;
    private String Summary;
    private String Description;

    public Recipe(){

    }

    public Recipe(String name, String summary, String description) {
        Name = name;
        Summary = summary;
        Description = description;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public String getSummary() {
        return Summary;
    }

    public String getDescription() {
        return Description;
    }
}
