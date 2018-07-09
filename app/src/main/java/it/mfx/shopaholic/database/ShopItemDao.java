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
    @Query("SELECT * FROM shopitems")
    List<ShopItem> getAllSync();

    @Query("SELECT * FROM shopitems")
    LiveData<List<ShopItem>> getAll();

    @Query("SELECT * FROM shopitems where id LIKE :id")
    ShopItem findById(String id);

    @Query("SELECT * FROM shopitems s, items i where i.id = s.itemid AND s.name LIKE :name")
    ShopItem findByName(String name);

    @Query("SELECT COUNT(*) from shopitems")
    int countItems();

    @Insert
    void insertAll(ShopItem... shopitems);

    @Delete
    void delete(ShopItem shopitem);
}
