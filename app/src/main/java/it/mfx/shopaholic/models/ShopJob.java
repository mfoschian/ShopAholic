package it.mfx.shopaholic.models;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "shops")
public class ShopJob {

    @PrimaryKey //(autoGenerate = true)
    @NonNull
    public String id;

    public boolean done = false;

    //From Version 10
    //public int date;

}
