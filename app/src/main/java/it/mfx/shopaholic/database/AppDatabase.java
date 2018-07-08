package it.mfx.shopaholic.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.util.List;
import java.util.UUID;


import it.mfx.shopaholic.models.ShopItem;

@Database(entities = {ShopItem.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static String dbName = "shopaholicDB";

    public abstract ShopItemDao itemsDao();

    public static AppDatabase newInstance(Context context) {
        RoomDatabase.Builder<AppDatabase> b = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, dbName);
        AppDatabase db = b.fallbackToDestructiveMigration()
                            .build();
        return db;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    public ShopItem addItem( ShopItem item ) {
        if( item.getId() == null ) {
            item.setId( newId() );
        }
        itemsDao().insertAll(item);
        return item;
    }

    public LiveData<List<ShopItem>> getLiveShopItems() {
          return itemsDao().getLiveAll();
    }

    public List<ShopItem> getShopItems() {
        return itemsDao().getAll();
    }
}
