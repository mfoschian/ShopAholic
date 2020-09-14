package it.mfx.shopaholic.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ShopDao {
    /*
    @Query("SELECT * FROM shops")
    List<Shop> getAllSync();

    @Query("SELECT * FROM shops")
    LiveData<List<Shop>> getAll();

    @Query("SELECT * FROM shops where id LIKE :id")
    Shop findById(String id);

    @Query("SELECT * FROM shops where name LIKE :name")
    Shop findByName(String name);

    @Query("SELECT COUNT(*) from shops")
    int countItems();

    @Insert
    void insertAll(Shop... shops);

    @Delete
    void delete(Shop shop);
    */

    @Query("SELECT DISTINCT shopName FROM shopitems where job_id is null order by shopName")
    List<String> getActiveNamesSync();

    @Query("SELECT DISTINCT shopName FROM items order by shopName")
    List<String> getNamesSync();

    @Query("SELECT DISTINCT zoneName FROM items where shopName = :shop_id order by zoneName")
    List<String> getZoneNamesSync(String shop_id);

}
