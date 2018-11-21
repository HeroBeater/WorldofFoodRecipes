package com.example.alex.worldoffoodrecipes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;

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
        TextView textTitle;
        public TextView textDesc;
        TextView textRate;
        ConstraintLayout parentLayout;

        MyViewHolder(View v){
            super(v);
            imageView = v.findViewById(R.id.imageView);
            textTitle = v.findViewById(R.id.textTitel);
            textDesc = v.findViewById(R.id.textDesc);
            textRate = v.findViewById(R.id.textViewRate);
            parentLayout = v.findViewById(R.id.recipeLayout);
        }
    }

    RecipeAdapter(ArrayList<Recipe> recipes, Context context){
        recipe_list = recipes;
        this.context = context;
    }

    //create new views
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recipe_element, parent, false);
        return new MyViewHolder(v);
    }

    //replace contents of a view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        Recipe recipe = recipe_list.get(position);

        if(recipe.getMainImage()==null){
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }else{
            holder.imageView.setImageBitmap(recipe.getMainImage());
        }

        holder.textTitle.setText(recipe.getName());
        holder.textDesc.setText(recipe.getKeyWords());
        holder.textRate.setText(String.valueOf(recipe.getRating_average()));

        final int pos = position;

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeShowActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("recipe_ID", recipe_list.get(pos).getID());
                intent.putExtra("recipe_user", recipe_list.get(pos).getAuthor());
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
