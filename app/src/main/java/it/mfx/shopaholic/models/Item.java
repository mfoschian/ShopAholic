package it.mfx.shopaholic.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "items")
public class Item {

    @PrimaryKey //(autoGenerate = true)
    @NonNull
    public String id;

    public String name;
    public String description;
    public String shopName;
    public String zoneName;

}
