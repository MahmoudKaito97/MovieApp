package com.example.mahmoud.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmoud.movieapp.Adapters.ImageAdapter;
import com.example.mahmoud.movieapp.Models.Movie;
import com.example.mahmoud.movieapp.Models.Review;
import com.example.mahmoud.movieapp.Models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainFragment extends Fragment {
    final String MOVIE_BASE_URL ="https://api.themoviedb.org/3/movie/";
    final String MOVIE_OVERVIEW="overview";
    final String MOVIE_RELEASE_DATE = "release_date";
    final String MOVIE_ORIGINAL_TITLE = "original_title";
    final String MOVIE_POSTER_PATH="poster_path";
    final String MOVIE_VOTE_AVERAGE = "vote_average";
    final String API_KEY = "api_key";
    final String TAG = "com.example.mahmoud:";
    ProgressBar Pb ;
    ArrayList<Movie> listOfAllData=new ArrayList<>();

    RecyclerView gridView;
    TextView empty;
    ImageAdapter imageAdapter;
    String sortType ="";
    static  boolean twoPane;
    int top = 0;
    int pop=0;
    int fav=0;
    public static Movie temp = new Movie();

public static DBHandler db ;

    public MainFragment() {
//        updateList();
        setHasOptionsMenu(true);
    }

    public MainFragment(String s) {
        sortType = s;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.fragment_main, container, false);
        Pb = (ProgressBar) rootView.findViewById(R.id.progressBar);
        empty = (TextView) rootView.findViewById(R.id.empty);
        imageAdapter = new ImageAdapter(getContext(),listOfAllData,MainActivity.twoPane);
        gridView = (RecyclerView) rootView.findViewById(R.id.grid_view);
        GridLayoutManager GM;
        if(getActivity().getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE ) {
             GM = new GridLayoutManager(getContext(), 3);
        }
        else {
            GM = new GridLayoutManager(getContext(),2);
        }
        gridView.setLayoutManager(GM);

        gridView.setAdapter(imageAdapter);

        db=new DBHandler(getContext());

         return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();



        }


    @Override
    public void onResume() {

        super.onResume();
        updateList();


        imageAdapter.setTwoPane(MainActivity.twoPane);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    String s="";
    public void updateList(){


        if(!(sortType.equals("favorite"))&&sortType!=s){
            listOfAllData.clear();

        new  FetchMovieCover().execute(sortType);
        }
        else if ((sortType.equals("favorite"))) {

            listOfAllData.clear();
            listOfAllData = db.getAllMovies();

            if(listOfAllData.isEmpty()){
                gridView.setVisibility(View.GONE);
                Pb.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }else{

                gridView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                Pb.setVisibility(View.VISIBLE);
            }

           // sortType="";

            imageAdapter.notifyDataSetChanged(listOfAllData);
            Pb.setVisibility(View.GONE);
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.action_settings){
            onResume();
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieCover extends AsyncTask< String,Void,ArrayList<Movie> >{



        HttpURLConnection urlConnection = null;

        String movieDescJsonStr = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gridView.setVisibility(View.GONE);
            Pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {


            try{

               connectToMovieDb(buildUri(params[0]));

                movieDescJsonStr=writeMovieDescription(urlConnection.getInputStream());

                Log.d(TAG,movieDescJsonStr);
                if(movieDescJsonStr==null){
                    return null;
                }
                listOfAllData= getDataFromJson(movieDescJsonStr);
 }
            catch (IOException e){
                Log.e(TAG,"IO exception",e);
                //Toast.makeText(getActivity(),"Something went wrong, Please! check your connection",Toast.LENGTH_LONG).show();

            }
            catch (JSONException e){
                Log.e(TAG,"JSON exception",e);
            }
            finally{
                if(urlConnection !=null)
                    urlConnection.disconnect();
            }

            return   listOfAllData;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            Pb.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            if(listOfAllData.isEmpty()){
                gridView.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }else{
                gridView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
            }
            imageAdapter.notifyDataSetChanged(movies);


        }

        public Uri buildUri(String sortType ){
            Uri uri = Uri.parse(MOVIE_BASE_URL+sortType+"?").buildUpon()
                    .appendQueryParameter(API_KEY,BuildConfig.MOVIE_DATABASE_API_KEY).build();
            return uri;
        }
        public void connectToMovieDb(Uri uri) throws IOException{


            URL url = new URL(uri.toString());

            urlConnection =(HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");

            urlConnection.connect();


        }

        public String writeMovieDescription(InputStream inputStream) throws IOException{

            String movieDescJsonStr;
            StringBuffer buffer = new StringBuffer();
            if(inputStream ==null){
                return null;
            }
           BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine())!=null){
                buffer.append(line+'\n');
            }
            if(buffer.length()==0){
                return null ;
            }

            movieDescJsonStr = buffer.toString();

            return movieDescJsonStr;
        }

        public ArrayList<Movie> getDataFromJson(String str) throws JSONException{


            JSONObject movieDescJson = new JSONObject(str);
            JSONArray results = movieDescJson.getJSONArray("results");

            ArrayList<Movie> sd = new ArrayList<>();

            for(int i=0;i<results.length();i++) {
                String posterPath = results.getJSONObject(i).getString(MOVIE_POSTER_PATH); //index = 0
                String originalTitle= results.getJSONObject(i).getString(MOVIE_ORIGINAL_TITLE); //index = 1
                String voteAverage= results.getJSONObject(i).getString(MOVIE_VOTE_AVERAGE); //index = 2
                String releaseDate= results.getJSONObject(i).getString(MOVIE_RELEASE_DATE); //index = 3
                String overview= results.getJSONObject(i).getString(MOVIE_OVERVIEW); //index = 4
                String movieId=results.getJSONObject(i).getString("id");
                Movie listOfSingleMovie=new Movie(posterPath,originalTitle,voteAverage,releaseDate,overview,movieId);

                sd.add(listOfSingleMovie);

            }

            return sd;
        }


    }


}
