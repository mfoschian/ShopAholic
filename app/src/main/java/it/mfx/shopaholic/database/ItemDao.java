package it.mfx.shopaholic.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import it.mfx.shopaholic.models.Item;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM items order by name COLLATE NOCASE ASC")
    List<Item> getAllSync();

    @Query("SELECT * FROM items order by name COLLATE NOCASE ASC")
    LiveData<List<Item>> getAll();

    @Query("SELECT * FROM items where id LIKE :id")
    Item findById(String id);

    @Query("SELECT * FROM items where name LIKE :name")
    List<Item> findByName(String name);

    @Query("SELECT COUNT(*) from items")
    int countItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Item... items);

    @Update
    void updateAll(Item... items);

    @Delete
    void delete(Item item);
}

