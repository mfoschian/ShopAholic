package it.mfx.shopaholic;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

import it.mfx.shopaholic.database.AppDatabase;
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
                    ShopItem x = new ShopItem();
                    x.setName("pippo");
                    x.setDescription("ciao ciao");
                    db().addItem(x);

                    x = new ShopItem();
                    x.setName("pluto");
                    x.setDescription("aufwiedersehen");
                    db().addItem(x);

                    cb.onSuccess( 2);
                }
                catch( Exception err ) {
                    cb.onError(err);
                }
            }
        });

    }

    public void getAsyncShopItems(@NonNull final Callback<List<ShopItem>> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<ShopItem> res = db().itemsDao().getAll();
                    cb.onSuccess(res);
                } catch (Exception err) {
                    cb.onError(err);
                }
            }
        });
    }

    public LiveData<List<ShopItem>> getLiveShopItems() {
        return db().getLiveShopItems();
    }

}
