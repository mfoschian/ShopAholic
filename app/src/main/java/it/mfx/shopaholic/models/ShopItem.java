package it.mfx.shopaholic.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "items")
public class ShopItem {

    @PrimaryKey //(autoGenerate = true)
    @NonNull
    private String id;

    private String name;

    private String description;
    private String shopName;
    private String zoneName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String n) {
        this.name = n;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getShopName() {
        return shopName;
    }
    public void setShopName(String n) {
        this.shopName = n;
    }

    public String getZoneName() {
        return zoneName;
    }
    public void setZoneName(String n) {
        this.zoneName = n;
    }


}
