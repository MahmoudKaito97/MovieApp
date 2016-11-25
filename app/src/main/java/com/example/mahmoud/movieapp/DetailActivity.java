package com.example.mahmoud.movieapp;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState==null)
     getSupportFragmentManager().beginTransaction().add(R.id.content_detail,new DetailFragment()).commit();

        
    }

}
