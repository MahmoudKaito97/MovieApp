package com.example.mahmoud.movieapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.mahmoud.movieapp.DBHandler;
import com.example.mahmoud.movieapp.DetailActivity;
import com.example.mahmoud.movieapp.DetailFragment;
import com.example.mahmoud.movieapp.MainActivity;
import com.example.mahmoud.movieapp.MainFragment;
import com.example.mahmoud.movieapp.Models.Movie;
import com.example.mahmoud.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private Context context ;
    private ArrayList<Movie> movieList ;
    private boolean twoPane ;

    public ImageAdapter(Context context,ArrayList<Movie> movieList,boolean tab){
        this.context = context;
        this.movieList =movieList ;
        twoPane = tab;
    }


    @Override
    public ImageAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(context).inflate(R.layout.grid_movie,parent,false);
        ImageHolder holder = new ImageHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ImageHolder holder, int position) {
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w342/"+(movieList.get(position)).getPath())
                .into(holder.imageView);
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @Override
    public int getItemCount() {
        return movieList.size();
    }


    public void notifyDataSetChanged(ArrayList<Movie> list) {
        movieList=list;
        super.notifyDataSetChanged();

    }
public void setTwoPane(boolean tab){
    twoPane = tab;
}
    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        public ImageHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.grid_movie_image_view);
        }

        @Override
        public void onClick(View v) {
            if(!twoPane){
                Intent intent = new Intent(context,DetailActivity.class);

                intent.putExtra("MovieObject",movieList.get(getLayoutPosition()));

                context.startActivity(intent);
            }
            else{

                DetailFragment.movie = movieList.get(getLayoutPosition());

                AppCompatActivity app = (AppCompatActivity) context;


                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_detail,new DetailFragment()).commit();

            }
        }
    }

 }

