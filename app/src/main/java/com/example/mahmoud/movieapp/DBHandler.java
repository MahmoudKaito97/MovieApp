package com.example.mahmoud.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;


import com.example.mahmoud.movieapp.Models.Movie;

import java.io.Serializable;
import java.util.ArrayList;



public class DBHandler extends SQLiteOpenHelper implements Serializable{

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "FavoriteInfo";
    private static final String TABLE_NAME = "Movie";
    private static final String KEY_ID = "id";
    private static final String KEY_PATH = "path";
    private static final String KEY_RATING= "Rating";
    private static final String KEY_DATE = "ReleaseDate";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_TITLE = "title";
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_RATING + " TEXT," + KEY_DATE + " TEXT,"
                + KEY_PATH + " TEXT," + KEY_OVERVIEW + " TEXT," +KEY_TITLE + " TEXT" + ");";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void addMovie(Movie movie) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, String.valueOf(movie.getMovieId()));
        values.put(KEY_RATING, String.valueOf(movie.getVoteAverage()));
        values.put(KEY_DATE, movie.getReleaseDate());
        values.put(KEY_PATH, movie.getPath());
        values.put(KEY_OVERVIEW, movie.getOverview());
        values.put(KEY_TITLE, movie.getTitle());

        db.insert(TABLE_NAME,null,values);
        db.close();
    }

    public boolean searchForMovie(int id){
        SQLiteDatabase db =getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+ KEY_TITLE + " FROM "+ TABLE_NAME +" WHERE "+KEY_ID +"="+id , null);
        if(cursor.getCount()==0){
            return false;
        }
        cursor.close();

        return true;

    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> movieList = new ArrayList<Movie>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();

                movie.setMovieId(Integer.parseInt(cursor.getString(0)));
                //movie.setFavorite(Boolean.parseBoolean(cursor.getString(1)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(1)));
                movie.setDate(cursor.getString(2));
                movie.setPath(cursor.getString(3));
                movie.setOverview(cursor.getString(4));
                movie.setTitle(cursor.getString(5));

                movieList.add(movie);

            } while (cursor.moveToNext());
        }


        return movieList;
    }
    public int getMoviesCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, movie.getMovieId());
       // values.put(KEY_FAV, movie.isFavorite());
        values.put(KEY_RATING, movie.getVoteAverage());
        values.put(KEY_DATE, movie.getReleaseDate());
        values.put(KEY_PATH, movie.getPath());
        values.put(KEY_OVERVIEW, movie.getOverview());
        values.put(KEY_TITLE, movie.getTitle());

        return db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[]{String.valueOf(movie.getMovieId())});
    }

    public void deleteMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[] { String.valueOf(movie.getMovieId()) });
        db.close();
    }

}
