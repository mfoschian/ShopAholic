package it.mfx.shopaholic.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import it.mfx.shopaholic.models.Item;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM items")
    List<Item> getAllSync();

    @Query("SELECT * FROM items")
    LiveData<List<Item>> getAll();

    @Query("SELECT * FROM items where id LIKE :id")
    Item findById(String id);

    @Query("SELECT * FROM items where name LIKE :name")
    Item findByName(String name);

    @Query("SELECT COUNT(*) from items")
    int countItems();

    @Insert
    void insertAll(Item... items);

    @Delete
    void delete(Item item);
}

