package com.example.mahmoud.movieapp;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mahmoud.movieapp.Adapters.ReviewAdapter;
import com.example.mahmoud.movieapp.Adapters.TrailerAdapter;
import com.example.mahmoud.movieapp.Models.Movie;
import com.example.mahmoud.movieapp.Models.Review;
import com.example.mahmoud.movieapp.Models.Trailer;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DetailFragment extends Fragment{


  public static  Movie movie = new Movie();
    ReviewAdapter revAdapter ;
    TrailerAdapter trailerAdapter;
    boolean orient = true;
    public DetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

if(savedInstanceState!=null){
    if(savedInstanceState.getParcelable("abc").equals(movie)){
        orient=false;
        movie = savedInstanceState.getParcelable("abc");
        revAdapter = new ReviewAdapter(getContext(),movie);
        trailerAdapter = new TrailerAdapter(getContext(),movie);
    }
    else
        orient=true;
}
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {

        if(!MainActivity.twoPane){
        movie = (Movie) getActivity().getIntent().getParcelableExtra("MovieObject");
        }

       // movie.getTrailers().clear();
        //movie.getReviews().clear();

        revAdapter = new ReviewAdapter(getContext(),movie);
        trailerAdapter = new TrailerAdapter(getContext(),movie);
      //  new Trailer_ReviewAsyncClass().execute(movie);

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
//        View header = inflater.inflate(R.layout.row_standard_detail,null,false);
        ShineButton shineButton = (ShineButton) rootView.findViewById(R.id.shine_button);
        shineButton.init(getActivity());
        TextView overview = (TextView) rootView.findViewById(R.id.overview);
        TextView date = (TextView) rootView.findViewById(R.id.release_date);
        TextView rate = (TextView) rootView.findViewById(R.id.rating);
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
        TextView title = (TextView) rootView.findViewById(R.id.title);

        //SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date dat=new Date();
        try {
             dat = formatter.parse(movie.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat f = new SimpleDateFormat("MMMM d, yyyy");



        if(MainFragment.db.searchForMovie(movie.getMovieId()))
        {
            shineButton.setChecked(true);
        }
        shineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainFragment.db.searchForMovie(movie.getMovieId())){
                   MainFragment.db.deleteMovie(movie);
                }
                else
                   MainFragment.db.addMovie(movie);
            }
        });


        Picasso.with(getContext())
                .load("http://image.tmdb.org/t/p/w185/"+(movie).getPath())
                .into(poster);

        overview.setText(movie.getOverview());
        date.setText(f.format(dat));
        rate.setText(""+Math.round( (movie.getVoteAverage()/2.0)*10.0)/10.0+" out of 5");
        title.setText(movie.getTitle());
        //new Trailer_ReviewAsyncClass().execute(movie);
        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        ratingBar.setRating(Float.parseFloat(""+Math.round( (movie.getVoteAverage()/2.0)*10.0)/10.0));

        RecyclerView trailerList = (RecyclerView) rootView.findViewById(R.id.trailer_recyclerView);
        trailerList.setLayoutManager(new LinearLayoutManager(getContext()));
        trailerList.setAdapter(trailerAdapter);

        RecyclerView reviewList = (RecyclerView) rootView.findViewById(R.id.review_recyclerView);
        reviewList.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewList.setAdapter(revAdapter);



        return rootView;
    }

    @Override
    public void onResume() {

        if(orient) {
            movie.getTrailers().clear();
            movie.getReviews().clear();
            new Trailer_ReviewAsyncClass().execute(movie);
        }
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("abc",movie);
    }

    class Trailer_ReviewAsyncClass extends AsyncTask<Movie,Void,Movie> {

        HttpURLConnection urlConnection=null;
        String trailerJson;
        String reviewJson;
        Movie m;
        @Override
        protected Movie doInBackground(Movie... params) {

             m = params[0];
            try{

                trailerJson=getTrailerJson(m.getMovieId());

                parseJsonToMovie(trailerJson,0);
            }
            catch (IOException e){

            }
            catch (JSONException e) {

            }
            finally {
                urlConnection.disconnect();
                urlConnection=null;
            }

            try{
                reviewJson=getReviewJson();

                parseJsonToMovie(reviewJson,1);
            }
            catch (IOException e){

            }
            catch (JSONException e) {

            }
            finally {
                urlConnection.disconnect();
            }

            return m;
        }


        @Override
        protected void onPostExecute(Movie mo) {


            super.onPostExecute(mo);
            movie = mo;
            revAdapter.notifyDataSetChanged(mo);
            trailerAdapter.notifyDataSetChanged(mo);

        }

        public String getTrailerJson(int id) throws IOException {

            Uri uri = Uri.parse("https://api.themoviedb.org/3/movie/"+id+"/videos?").buildUpon().
                    appendQueryParameter("api_key", BuildConfig.MOVIE_DATABASE_API_KEY).build();

            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream ==null){
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuffer buffer = new StringBuffer();

            while((line=reader.readLine())!=null){
                buffer.append(line+'\n');
            }
            if(buffer.length()==0){
                return null ;
            }

            return buffer.toString();
        }
        public String getReviewJson()throws IOException{
            Uri uri = Uri.parse("https://api.themoviedb.org/3/movie/"+movie.getMovieId()+"/reviews?").buildUpon().
                    appendQueryParameter("api_key",BuildConfig.MOVIE_DATABASE_API_KEY).build();

            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream ==null){
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuffer buffer = new StringBuffer();

            while((line=reader.readLine())!=null){
                buffer.append(line+'\n');
            }
            if(buffer.length()==0){
                return null ;
            }

            return buffer.toString();
        }

        public void parseJsonToMovie(String Json, int mode) throws JSONException {

            JSONObject obj = new JSONObject(Json);

            JSONArray array= obj.getJSONArray("results");
            if(mode == 0){

                ArrayList<Trailer> trailerTempArray = new ArrayList<>();

                for(int i=0;i<array.length();i++){
                    if(array.getJSONObject(i).getString("type").equals("Trailer")){
                        Trailer trailer = new Trailer(
                                array.getJSONObject(i).getString("name"),
                                array.getJSONObject(i).getString("key")
                        );

                        trailerTempArray.add(trailer);
                       }
                }
                m.setTrailers(trailerTempArray);
                }
            else{
                ArrayList<Review> reviewTempArray = new ArrayList<>();
                for(int i=0;i<array.length();i++){
                    Review review = new Review(
                            array.getJSONObject(i).getString("author"),
                            array.getJSONObject(i).getString("content")
                    );
                    reviewTempArray.add(review);
                  }

                m.setReviews(reviewTempArray);
                   }

        }


    }
}
