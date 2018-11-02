package com.example.alex.worldoffoodrecipes;

import android.content.Context;
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
        public ConstraintLayout parentLayout;

        public MyViewHolder(View v){
            super(v);
            titleOfReview = v.findViewById(R.id.titleOfReview);
            userOfReview = v.findViewById(R.id.userOfReview);
            descriptionOfReview = v.findViewById(R.id.descriptionOfReview);
            ratingBarOfReview = v.findViewById(R.id.ratingBarOfReview);
            parentLayout = v.findViewById(R.id.reviewLayout);
        }
    }

    public ReviewAdapter(ArrayList<Review> reviews, Context context){
        review_list = reviews;
        this.context = context;
    }

    //create new views
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_review_element, parent, false);
        MyViewHolder vH = new MyViewHolder(v);
        return vH;
    }

    //replace contents of a view
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        Review review = review_list.get(position);
        holder.userOfReview.setText(review.getUsernameOfReview());
        holder.ratingBarOfReview.setRating(review.getRating());
        if (review.getTitleOfReview().equals("")){
            holder.titleOfReview.setHeight(0);
        }else{
            holder.titleOfReview.setText(review.getTitleOfReview());
        }
        if (review.getDescriptionOfReview().equals("")){
            holder.descriptionOfReview.setHeight(0);
        }else{
            holder.descriptionOfReview.setText(review.getTitleOfReview());
        }
    }

    //size of list
    @Override
    public int getItemCount(){
        return review_list.size();
    }
}
