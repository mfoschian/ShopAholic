package it.mfx.shopaholic.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.RoomWarnings;
import android.support.annotation.NonNull;

@Entity(tableName = "shopitems")
public class ShopItem {

    @PrimaryKey //(autoGenerate = true)
    @NonNull
    public String sid;

    public String itemid;

    public int qty = 0;

    @Embedded
    @SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
    public Item item;


}
