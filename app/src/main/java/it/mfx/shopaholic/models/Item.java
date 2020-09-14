package it.mfx.shopaholic.models;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;


@Entity(tableName = "items",
        indices = {
            @Index(value = "name"),
            @Index(value = "shopName"),
            @Index(value = "zoneName")
        })
public class Item {

    @PrimaryKey //(autoGenerate = true)
    @NonNull
    public String id;

    public String name;
    public String description;
    public String shopName;
    public String zoneName;

}
