package com.example.mahmoud.movieapp.Adapters;


import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mahmoud.movieapp.DBHandler;

import com.example.mahmoud.movieapp.Models.Movie;

import com.example.mahmoud.movieapp.Models.Review;
import com.example.mahmoud.movieapp.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;



public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private Movie movie;
    private Context context;
    public ReviewAdapter(Context c,Movie m){
        context=c;
        movie=m;
    }


    @Override
    public ReviewAdapter.ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_review_list_view,parent,false);
        ReviewAdapter.ReviewHolder RHolder = new ReviewAdapter.ReviewHolder(v);
        return RHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewHolder holder, int position) {
        holder.tv1.setText(movie.getReviews().get(position).getName());
        holder.tv2.setText(movie.getReviews().get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return movie.getReviews().size();
    }


    public void notifyDataSetChanged(Movie mo) {
        super.notifyDataSetChanged();
        movie=mo;
    }
    class ReviewHolder extends RecyclerView.ViewHolder {
        TextView tv1;
        ExpandableTextView tv2;
        public ReviewHolder(View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.reviewer_name);

            tv2 = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
        }


    }
}
