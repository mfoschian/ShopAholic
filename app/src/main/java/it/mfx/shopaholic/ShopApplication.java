package it.mfx.shopaholic;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

import it.mfx.shopaholic.database.AppDatabase;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShopItem;

public class ShopApplication extends Application {

    private AppDatabase db = null;

    AppDatabase db() {
        if( db == null ) {
            db = AppDatabase.newInstance( this.getApplicationContext());
        }
        return db;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        insertTestData(new Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    public interface Callback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public void insertTestData(@NonNull final Callback<Integer> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    int n = db().itemDao().countItems();
                    if( n == 0 ) {
                        Item x = new Item();
                        x.name = "Burro";
                        x.description = "Latterie friulane";
                        x.shopName = "A&O";
                        x.zoneName = "frigo";
                        x = db().addItem(x);

                        Item y = new Item();
                        y.name = "Latte";
                        y.description = "Zymil";
                        x.shopName = "A&O";
                        y = db().addItem(y);

                        ShopItem s = new ShopItem();
                        s.item_id = x.id;
                        s.qty = 1;
                        s = db().addShopItem(s);

                        ShopItem q = new ShopItem();
                        q.item_id = y.id;
                        q.qty = 1;
                        q = db().addShopItem(q);

                        cb.onSuccess(2);
                    }
                    cb.onSuccess(0);
                }
                catch( Exception err ) {
                    cb.onError(err);
                }
            }
        });

    }


    public void getAsyncShopItems(@NonNull final Callback<List<ShopItem>> cb) {
        insertTestData(new Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                List<ShopItem> res = db().shopItemDao().getAllSync();
                cb.onSuccess(res);

            }

            @Override
            public void onError(Exception e) {
                cb.onError(e);
            }
        });
        /*
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<ShopItem> res = db().shopItemDao().getAllSync();
                    cb.onSuccess(res);
                } catch (Exception err) {
                    cb.onError(err);
                }
            }
        });
        */
    }

    public LiveData<List<ShopItem>> getLiveShopItems() {
        return db().getLiveShopItems();
    }

}
