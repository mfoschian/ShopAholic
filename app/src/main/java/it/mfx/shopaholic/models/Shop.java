package it.mfx.shopaholic.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "shops")
public class Shop {

    @PrimaryKey //(autoGenerate = true)
    @NonNull
    public String id;

    public String name;

    // Orari di apertura....
    // Posizione GPS ...
    //
}
