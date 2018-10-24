package com.example.alex.worldoffoodrecipes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder>{
    private ArrayList<Recipe> recipe_list;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textTitel;
        public TextView textDesc;

        public MyViewHolder(View v){
            super(v);
            imageView = v.findViewById(R.id.imageView);
            textTitel = v.findViewById(R.id.textTitel);
            textDesc = v.findViewById(R.id.textDesc);
        }
    }

    public RecipeAdapter(ArrayList<Recipe> recipes){
        recipe_list = recipes;
    }

    //create new views
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recipe_element, parent, false);
        MyViewHolder vH = new MyViewHolder(v);
        return vH;
    }

    //replace contents of a view
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Recipe recipe = recipe_list.get(position);
        holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        holder.textTitel.setText(recipe.getName());
        holder.textDesc.setText(recipe.getSummary());
    }

    //size of list
    @Override
    public int getItemCount(){
        return recipe_list.size();
    }
}
