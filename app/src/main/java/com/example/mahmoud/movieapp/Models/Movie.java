package com.example.mahmoud.movieapp.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mahmoud on 8/29/2016.
 */

public class Movie implements Parcelable{

    private String overview,path,releaseDate,title;
    private double voteAverage;
    private int id;
    private ArrayList<Trailer>  trailers = new ArrayList<>();
    private ArrayList<Review>   reviews = new ArrayList<>();

    
    public Movie(){
        
    }
    public Movie(String path , String title, String voteAverage , String releaseDate, String overview, String id){

        this.overview=overview;
        this.path=path;
        this.releaseDate=releaseDate;
        this.voteAverage=Double.parseDouble(voteAverage);
        this.id=Integer.parseInt(id);
        this.title=title;
    }

    public Movie(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        overview = data[0];
        path = data[1];
        releaseDate = data[2];
        title = data[3];
        voteAverage = in.readDouble();
        id = in.readInt();

    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public double getVoteAverage() {
        return voteAverage;
    }
    public void setVoteAverage(double rate) {
        voteAverage=rate;
    }


    public String getReleaseDate() {
        return releaseDate;
    }



    public String getPath() {
        return path;
    }


    public String getOverview() {
        return overview;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public void setMovieId(int id){
        this.id=id;
    }
    public int getMovieId(){
        return id;
    }
    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {

        boolean b = this.trailers.addAll(trailers);
     Log.v("DB","trailers Added"+b);
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {

        boolean b=this.reviews.addAll(reviews);
        Log.v("DB","reviews Added"+b);
    }


    public void setDate(String date) {
        releaseDate= date;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{overview,path,releaseDate,title});
        dest.writeDouble(voteAverage);
        dest.writeInt(id);

    }
}
