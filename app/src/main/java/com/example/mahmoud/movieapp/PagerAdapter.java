package com.example.mahmoud.movieapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Mahmoud on 10/25/2016.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
   public PagerAdapter(FragmentManager fm){
       super(fm);

   }
    @Override
    public Fragment getItem(int position) {

        if(position==0){
            return new MainFragment("popular");
        }else if(position==1){
            return new MainFragment("top_rated");

        }
        else if(position ==2){
            return new MainFragment("favorite");
        }
        else
            return null;
    }
    @Override
    public int getCount() {
        return 3;
    }
}
