package com.example.alex.worldoffoodrecipes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{
    private ArrayList<Review> review_list;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView titleOfReview, userOfReview, descriptionOfReview;
        public RatingBar ratingBarOfReview;
        ConstraintLayout parentLayout;

        MyViewHolder(View v){
            super(v);
            titleOfReview = v.findViewById(R.id.titleOfReview);
            userOfReview = v.findViewById(R.id.userOfReview);
            descriptionOfReview = v.findViewById(R.id.descriptionOfReview);
            ratingBarOfReview = v.findViewById(R.id.ratingBarOfReview);
            parentLayout = v.findViewById(R.id.reviewLayout);
        }
    }

    ReviewAdapter(ArrayList<Review> reviews, Context context){
        review_list = reviews;
        this.context = context;
    }

    //create new views
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_review_element, parent, false);
        return new MyViewHolder(v);
    }

    //replace contents of a view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        Review review = review_list.get(position);
        holder.userOfReview.setText(review.getUsernameOfReview());
        holder.ratingBarOfReview.setRating(review.getRating());
        if (review.getTitleOfReview().equals("")){
            holder.titleOfReview.setVisibility(View.GONE);
            holder.descriptionOfReview.setVisibility(View.GONE);
        }else{
            holder.titleOfReview.layout(16,8,16,0);
            holder.titleOfReview.setText(review.getTitleOfReview());
            holder.descriptionOfReview.layout(16,12,16,8);
            holder.descriptionOfReview.setText(review.getTitleOfReview());
        }

        final int pos = position;

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, addReviewActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("recipe_ID", review_list.get(pos).getTimestamp());
                intent.putExtra("recipe_user", review_list.get(pos).getDescriptionOfReview());
                context.startActivity(intent);
            }
        });
    }

    //size of list
    @Override
    public int getItemCount(){
        return review_list.size();
    }
}
