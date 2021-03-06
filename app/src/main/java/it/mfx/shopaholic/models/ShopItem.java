package it.mfx.shopaholic.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;
import android.arch.persistence.room.RoomWarnings;
import android.support.annotation.NonNull;



@Entity(tableName = "shopitems",
        indices = {
                @Index(value = "item_id"),
                @Index(value = "status"),
                @Index(value = "job_id")
        })
public class ShopItem {

    @PrimaryKey //(autoGenerate = true)
    @NonNull
    public String sid;

    public String item_id;

    public int qty = 0;

    //From Version 10
    public class Status {
        public static final int PENDING = 0;
        public static final int DONE = 1;
    }

    public int status = Status.PENDING;

    public String job_id;

    //@Embedded(prefix = "item")
    @Embedded
    @SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
    public Item item;


}
