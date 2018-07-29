package it.mfx.shopaholic.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import it.mfx.shopaholic.models.ShopItem;

@Dao
public interface ShopItemDao {
    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) order by i.name")
    List<ShopItem> getAllSync();

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) order by i.name")
    LiveData<List<ShopItem>> getAll();

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) where s.job_id is null order by i.name")
    List<ShopItem> getActiveSync();

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) where s.job_id is null order by i.name")
    LiveData<List<ShopItem>> getActive();

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) where s.job_id is null order by i.zoneName, i.name")
    List<ShopItem> getActiveByZoneSync();

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) where s.job_id is null order by i.zoneName, i.name")
    LiveData<List<ShopItem>> getActiveByZone();

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) where s.job_id = :job_id order by i.name")
    LiveData<List<ShopItem>> getForJob(String job_id);

    @Query("SELECT COUNT(*) FROM shopitems where item_id = :item_id")
    int countForItem(String item_id);


    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) where s.item_id LIKE :id")
    ShopItem findByItemId(String id);

    @Query("SELECT * FROM shopitems s LEFT JOIN items i ON (i.id = s.item_id) WHERE s.name LIKE :name order by i.name")
    ShopItem findByItemName(String name);

    @Query("SELECT COUNT(*) from shopitems")
    int countItems();

    @Query("SELECT COUNT(*) from shopitems where job_id is null")
    int countActive();

    @Query("SELECT COUNT(*) from shopitems where job_id is null and status = :status")
    int countActiveByStatus(int status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ShopItem... shopitems);

    @Delete
    void delete(ShopItem shopitem);

    @Update
    void update(ShopItem... shopItems);

    @Update
    void update(List<ShopItem>  shopItems);

}
