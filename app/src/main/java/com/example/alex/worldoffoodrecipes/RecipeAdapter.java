package com.example.alex.worldoffoodrecipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder>{
    private ArrayList<Recipe> recipe_list;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textTitle;
        public TextView textDesc;
        public TextView textRate;
        public ConstraintLayout parentLayout;

        public MyViewHolder(View v){
            super(v);
            imageView = v.findViewById(R.id.imageView);
            textTitle = v.findViewById(R.id.textTitel);
            textDesc = v.findViewById(R.id.textDesc);
            textRate = v.findViewById(R.id.textViewRate);
            parentLayout = v.findViewById(R.id.recipeLayout);
        }
    }

    public RecipeAdapter(ArrayList<Recipe> recipes, Context context){
        recipe_list = recipes;
        this.context = context;
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
    public void onBindViewHolder(MyViewHolder holder, final int position){
        Recipe recipe = recipe_list.get(position);
        holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        holder.textTitle.setText(recipe.getName());
        holder.textDesc.setText(recipe.getSummary());
        holder.textRate.setText(String.valueOf(recipe.getRating_average()));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeShowActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("recipe_name", recipe_list.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    //size of list
    @Override
    public int getItemCount(){
        return recipe_list.size();
    }
}
