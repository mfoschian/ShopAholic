package it.mfx.shopaholic.models;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.RoomWarnings;
import androidx.annotation.NonNull;



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
