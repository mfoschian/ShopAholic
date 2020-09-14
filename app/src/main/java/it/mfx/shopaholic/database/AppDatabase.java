package it.mfx.shopaholic.database;

import androidx.lifecycle.LiveData;
import androidx.room.Database;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;

import java.util.List;
import java.util.UUID;


import it.mfx.shopaholic.database.migrations.Migration_09_to_10;
import it.mfx.shopaholic.database.migrations.Migration_10_to_11;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShopItem;

@Database(entities = {Item.class, ShopItem.class}, version = 11, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static String dbName = "shopaholicDB";

    public abstract ItemDao itemDao();
    public abstract ShopItemDao shopItemDao();
    public abstract ShopDao shopDao();

    public static AppDatabase newInstance(Context context) {

        Migration M_09_10 = new Migration_09_to_10();
        Migration M_10_11 = new Migration_10_to_11();


        RoomDatabase.Builder<AppDatabase> b = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, dbName);
        AppDatabase db = b

                .addMigrations( M_09_10, M_10_11  )
                .fallbackToDestructiveMigration()
                .build();

        return db;
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }

    public void saveItem( Item item ) {
        itemDao().updateAll(item);
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

    public Item getItem(String item_id) {
        return itemDao().findById(item_id);
    }

    public boolean hasShopItem(String item_id) {
        int count  = shopItemDao().countForItem(item_id);
        return count > 0;
    }

    public void deleteItem(Item item) {
        if( item == null )
            return;

        itemDao().delete(item);
    }



    public ShopItem addShopItem( ShopItem item ) {
        if( item.sid == null ) {
            item.sid = newId();
        }
        shopItemDao().insertAll(item);
        return item;
    }

    public void deleteShopItem(ShopItem item) {
        shopItemDao().delete(item);
    }


    public LiveData<List<ShopItem>> getShopItemsLive() { return shopItemDao().getActive(); }

    public List<ShopItem> getShopItems() { return shopItemDao().getAllSync(); }
    public List<ShopItem> getActiveShopItems() { return shopItemDao().getActiveSync(); }

    public List<ShopItem> getActiveShopItemsByZone() { return shopItemDao().getActiveByZoneSync(); }
    public List<ShopItem> getActiveShopItemsByName() { return shopItemDao().getActiveByNameSync(); }

    public void saveShopItems(List<ShopItem> shopitems ) {
        shopItemDao().update(shopitems);
    }
    public void saveShopItem(ShopItem shopitem) {
        shopItemDao().update(shopitem);
    }

    public List<String> getShopNames() {
        return shopDao().getNamesSync();
    }

    public List<String> getActiveShopNames() {
        return shopDao().getActiveNamesSync();
    }

    public List<String> getZoneNames(String shop_id) {
        return shopDao().getZoneNamesSync(shop_id);
    }

}
