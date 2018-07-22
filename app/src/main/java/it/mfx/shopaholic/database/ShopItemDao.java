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
    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id)")
    List<ShopItem> getAllSync();

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id)")
    LiveData<List<ShopItem>> getAll();

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) where s.item_id LIKE :id")
    ShopItem findByItemId(String id);

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) WHERE s.name LIKE :name")
    ShopItem findByItemName(String name);

    @Query("SELECT COUNT(*) from shopitems")
    int countItems();

    @Insert
    void insertAll(ShopItem... shopitems);

    @Delete
    void delete(ShopItem shopitem);

}
