package com.example.mahmoud.movieapp.Models;

import java.io.Serializable;

/**
 * Created by Mahmoud on 9/7/2016.
 */

public class Trailer implements Serializable{
    private String name;
    private String link;
    public Trailer(String name,String key){
        this.name=name;
        link="https://www.youtube.com/watch?v="+key;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
