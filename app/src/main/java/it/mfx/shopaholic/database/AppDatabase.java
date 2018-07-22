package it.mfx.shopaholic.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.util.List;
import java.util.UUID;


import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShopItem;

@Database(entities = {Item.class, ShopItem.class}, version = 9, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static String dbName = "shopaholicDB";

    public abstract ItemDao itemDao();
    public abstract ShopItemDao shopItemDao();

    public static AppDatabase newInstance(Context context) {
        RoomDatabase.Builder<AppDatabase> b = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, dbName);
        AppDatabase db = b.fallbackToDestructiveMigration()
                            .build();
        return db;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    public Item addItem( Item item ) {
        if( item.id == null ) {
            item.id = newId();
        }
        itemDao().insertAll(item);
        return item;
    }

    public LiveData<List<Item>> getLiveItems() { return itemDao().getAll(); }

    public List<Item> getItems() { return itemDao().getAllSync(); }





    public ShopItem addShopItem( ShopItem item ) {
        if( item.sid == null ) {
            item.sid = newId();
        }
        shopItemDao().insertAll(item);
        return item;
    }

    public LiveData<List<ShopItem>> getLiveShopItems() { return shopItemDao().getAll(); }

    public List<ShopItem> getShopItems() { return shopItemDao().getAllSync(); }

    public void saveShopItems(List<ShopItem> shopitems ) {
        shopItemDao().update(shopitems);
    }
}
