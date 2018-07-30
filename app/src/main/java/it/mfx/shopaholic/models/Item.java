package it.mfx.shopaholic.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


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
