package it.mfx.shopaholic;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.telecom.Call;

import java.util.List;

import it.mfx.shopaholic.database.AppDatabase;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.Shop;
import it.mfx.shopaholic.models.ShopItem;

public class ShopApplication extends Application {

    private AppDatabase db = null;


    public final class IntentRequests {
        final public static int CHOOSE_ITEM_REQUEST = 8000;
        final public static int EDIT_ITEM_REQUEST = 8001;
    }

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

    public interface CallbackSimple {
        void onSuccess();
        void onError(Exception e);
    }

    public void insertTestData(@NonNull final Callback<Integer> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    int n = db().itemDao().countItems();
                    if( n == 0 ) {
                        /*
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
                        */

                        int item_to_insert = 20;
                        for( int i=0; i<item_to_insert; i++ ) {
                            Item item = new Item();
                            item.name = "Test " + i;
                            item.shopName = "A&O";
                            item.description = "Descrizione " + i;
                            db().addItem(item);

                            ShopItem si = new ShopItem();
                            si.item_id = item.id;
                            si.qty = 1;
                            db().addShopItem(si);
                        }

                        cb.onSuccess(item_to_insert);
                    }
                    cb.onSuccess(0);
                }
                catch( Exception err ) {
                    cb.onError(err);
                }
            }
        });

    }

    //==============================================
    //  Items
    //==============================================

    public List<Item> getItems() {
        List<Item> res = db().itemDao().getAllSync();
        return res;
    }

    public void getItemsAsync(@NonNull final Callback<List<Item>> cb) {
        insertTestData(new Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                List<Item> res = getItems();
                cb.onSuccess(res);
            }

            @Override
            public void onError(Exception e) {
                cb.onError(e);
            }
        });
    }

    public void getItemByIdAsync(@NonNull final String item_id, @NonNull final Callback<Item> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Item res = db().itemDao().findById(item_id);
                    cb.onSuccess(res);
                } catch (Exception err) {
                    cb.onError(err);
                }
            }
        });
    }

    public void getItemsByNameAsync(final String filter, @NonNull final Callback<List<Item>> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Item> res = db().itemDao().findByName(filter);
                    cb.onSuccess(res);
                } catch (Exception err) {
                    cb.onError(err);
                }
            }
        });
    }

    public void saveItem( Item item ) {
        db().saveItem(item);
    }

    public void saveItemAsync(final Item item, final Callback<Boolean> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    saveItem(item);
                    if( cb != null )
                        cb.onSuccess(true);
                } catch (Exception err) {
                    if( cb != null )
                        cb.onError(err);
                }
            }
        });
    }

    public void addItem( Item item ) {
        db().addItem(item);
    }

    public void addItemAsync(final Item item, final Callback<Boolean> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    addItem(item);
                    if( cb != null )
                        cb.onSuccess(true);
                } catch (Exception err) {
                    if( cb != null )
                        cb.onError(err);
                }
            }
        });
    }


    //==============================================
    //  ShopItems
    //==============================================

    public List<ShopItem> getShopItems() {
        List<ShopItem> res = db().shopItemDao().getAllSync();
        return res;
    }

    public void getShopItemsAsync(@NonNull final Callback<List<ShopItem>> cb) {
        insertTestData(new Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                List<ShopItem> res = getShopItems();
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

    public void saveShopItems( List<ShopItem> shopItems ) {
        db().saveShopItems(shopItems);
    }

    public void saveShopItem( ShopItem shopItem ) {
        db().saveShopItem(shopItem);
    }

    public void saveShopItemsAsync(final List<ShopItem> shopItems, final Callback<Integer> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    saveShopItems(shopItems);
                    if( cb != null )
                        cb.onSuccess(0);
                } catch (Exception err) {
                    if( cb != null )
                        cb.onError(err);
                }
            }
        });
    }

    public void saveShopItemAsync(final ShopItem shopItem, final Callback<Boolean> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    saveShopItem(shopItem);
                    if( cb != null )
                        cb.onSuccess(true);
                } catch (Exception err) {
                    if( cb != null )
                        cb.onError(err);
                }
            }
        });
    }

    public void addShopItemAsync(final ShopItem shopItem, final Callback<Boolean> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    db().addShopItem(shopItem);
                    if( cb != null )
                        cb.onSuccess(true);
                } catch (Exception err) {
                    if( cb != null )
                        cb.onError(err);
                }
            }
        });
    }

}
