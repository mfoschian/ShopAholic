package it.mfx.shopaholic.models;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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
