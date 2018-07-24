package it.mfx.shopaholic.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "shops")
public class ShopJob {

    @PrimaryKey //(autoGenerate = true)
    @NonNull
    public String id;

    public boolean done = false;

    //From Version 10
    //public int date;

}
