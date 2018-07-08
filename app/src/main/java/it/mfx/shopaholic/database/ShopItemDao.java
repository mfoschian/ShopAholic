package it.mfx.shopaholic.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import it.mfx.shopaholic.models.ShopItem;

@Dao
public interface ShopItemDao {
    @Query("SELECT * FROM items")
    List<ShopItem> getAll();

    @Query("SELECT * FROM items")
    LiveData<List<ShopItem>> getLiveAll();

    @Query("SELECT * FROM items where id LIKE :id")
    ShopItem findById(String id);

    @Query("SELECT * FROM items where name LIKE :name")
    ShopItem findByName(String name);

    @Query("SELECT COUNT(*) from items")
    int countItems();

    @Insert
    void insertAll(ShopItem... items);

    @Delete
    void delete(ShopItem item);
}
