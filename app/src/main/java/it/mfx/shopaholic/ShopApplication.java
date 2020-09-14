package it.mfx.shopaholic;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import it.mfx.shopaholic.database.AppDatabase;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShareableData;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.utils.FileUtils;
import it.mfx.shopaholic.utils.ShareUtils;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ShopApplication extends Application {

    private AppDatabase db = null;


    public final class IntentRequests {
        final public static int CHOOSE_ITEM_REQUEST = 8000;
        final public static int EDIT_ITEM_REQUEST = 8001;
        final public static int SHOP_RUN_REQUEST = 8002;
        final public static int PERMISSIONS_REQUEST = 8003;
        final public static int CHOOSE_IMPORT_FILE_REQUEST = 8004;
    }

    AppDatabase db() {
        if (db == null) {
            db = AppDatabase.newInstance(this.getApplicationContext());
        }
        return db;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        insertTestData(new Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
        */
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
                    if (n == 0) {

                        int item_to_insert = 20;
                        for (int i = 0; i < item_to_insert; i++) {
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
                } catch (Exception err) {
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
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Item> res = getItems();
                    cb.onSuccess(res);
                } catch (Exception err) {
                    cb.onError(err);
                }
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

    public void saveItem(Item item) {
        db().saveItem(item);
    }

    public void saveItemAsync(final Item item, final Callback<Boolean> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    saveItem(item);
                    if (cb != null)
                        cb.onSuccess(true);
                } catch (Exception err) {
                    if (cb != null)
                        cb.onError(err);
                }
            }
        });
    }

    public void addItem(Item item) {
        db().addItem(item);
    }

    public void addItemAsync(final Item item, final Callback<Boolean> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    addItem(item);
                    if (cb != null)
                        cb.onSuccess(true);
                } catch (Exception err) {
                    if (cb != null)
                        cb.onError(err);
                }
            }
        });
    }

    public void deleteItem(Item item) {
        db().deleteItem(item);
    }

    public boolean isItemDeletable(String item_id) {
        boolean ok = !db().hasShopItem(item_id);
        return ok;
    }


    //==============================================
    //  ShopItems
    //==============================================

    public List<ShopItem> getShopItems() {
        List<ShopItem> res = db().getActiveShopItems();
        return res;
    }

    public void getShopItemsAsync(@NonNull final Callback<List<ShopItem>> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<ShopItem> res = getShopItems();
                    cb.onSuccess(res);
                } catch (Exception err) {
                    cb.onError(err);
                }
            }
        });
    }

    public List<ShopItem> getShopItemsByZone() {
        List<ShopItem> res = db().getActiveShopItemsByZone();
        return res;
    }

    public void getShopItemsByZoneAsync(@NonNull final Callback<List<ShopItem>> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<ShopItem> res = getShopItemsByZone();
                    cb.onSuccess(res);
                } catch (Exception err) {
                    cb.onError(err);
                }
            }
        });
    }

    public List<ShopItem> getShopItemsByName() {
        List<ShopItem> res = db().getActiveShopItemsByName();
        return res;
    }

    public void getShopItemsByNameAsync(@NonNull final Callback<List<ShopItem>> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<ShopItem> res = getShopItemsByName();
                    cb.onSuccess(res);
                } catch (Exception err) {
                    cb.onError(err);
                }
            }
        });
    }

    public void saveShopItems(List<ShopItem> shopItems) {
        db().saveShopItems(shopItems);
    }

    public void saveShopItem(ShopItem shopItem) {
        db().saveShopItem(shopItem);
    }

    public void saveShopItemsAsync(final List<ShopItem> shopItems, final Callback<Integer> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    saveShopItems(shopItems);
                    if (cb != null)
                        cb.onSuccess(0);
                } catch (Exception err) {
                    if (cb != null)
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
                    if (cb != null)
                        cb.onSuccess(true);
                } catch (Exception err) {
                    if (cb != null)
                        cb.onError(err);
                }
            }
        });
    }

    public void addShopItem(ShopItem item) {
        db().addShopItem(item);
    }

    public void addShopItemAsync(final ShopItem shopItem, final Callback<Boolean> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    addShopItem(shopItem);
                    if (cb != null)
                        cb.onSuccess(true);
                } catch (Exception err) {
                    if (cb != null)
                        cb.onError(err);
                }
            }
        });
    }

    public void deleteShopItem(ShopItem item) {
        db().deleteShopItem(item);
    }

    public boolean isShopItemDeletable(String item_id) {
        //boolean ok = ! db().hasShopItem(item_id);
        //return ok;
        // TODO: check if there is a job referenced ...
        return true;
    }

    //==============================================
    //  Shops
    //==============================================


    public List<String> getShopNames() {
        return db().getShopNames();
    }

    public void getShopNamesAsync(final Callback<List<String>> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> names = getShopNames();
                    if (cb != null)
                        cb.onSuccess(names);
                } catch (Exception err) {
                    if (cb != null)
                        cb.onError(err);
                }
            }
        });
    }

    public List<String> getActiveShopNames() {
        return db().getActiveShopNames();
    }

    public void getActiveShopNamesAsync(final Callback<List<String>> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> names = getActiveShopNames();
                    if (cb != null)
                        cb.onSuccess(names);
                } catch (Exception err) {
                    if (cb != null)
                        cb.onError(err);
                }
            }
        });
    }

    public List<String> getShopZoneNames(String shop_name) {
        return db().getZoneNames(shop_name);
    }

    public void getShopZoneNamesAsync(final String shop_name, final Callback<List<String>> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> names = getShopZoneNames(shop_name);
                    if (cb != null)
                        cb.onSuccess(names);
                } catch (Exception err) {
                    if (cb != null)
                        cb.onError(err);
                }
            }
        });
    }


    //==============================================
    //  Import Data
    //==============================================
    public void importData(final ShareableData data) {
        if (data == null)
            return;

        if (data.items != null) {
            for (Item item : data.items) {
                //saveItem(item);
                addItem(item);
            }
        }

        if (data.shopItems != null) {
            for (ShopItem shopItem : data.shopItems) {
                //saveShopItem(shopItem);
                addShopItem(shopItem);
            }
        }
    }


    private boolean isAcceptedImportType(String type) {
        if( type == null )
            return true;

        final String[] acceptedImportTypes = new String[] {
                getString(R.string.share_mime_type),
                "text/html",
                "application/json",
                "application/octet-stream"
        };


        boolean type_accepted = false;
        for( String acceptedType: acceptedImportTypes) {
            if( acceptedType.equals(type)) {
                type_accepted = true;
                break;
            }
        }

        return type_accepted;
    }

    private boolean isAcceptedAction(String action) {
        if( action == null )
            // Can be an intent returned from CHOOSE_FILE request ...
            return true;

        if(Intent.ACTION_SEND.equals(action)
                || Intent.ACTION_VIEW.equals(action))
            return true;
        else
            return false;
    }


    public boolean importDataAsync(final Intent intent, final CallbackSimple cb) {
        try {
            //Check shared data
            String action = intent.getAction();
            String type = intent.getType();

            if(isAcceptedAction(action) && isAcceptedImportType(type)) {

                // Load data shared from outside
                String json = intent.getStringExtra(Intent.EXTRA_TEXT);
                if( json != null ) {
                    importDataAsync(json, cb);
                    return true;
                }
                // maybe a file was passed
                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (uri == null)
                    uri = intent.getData();

                if (uri != null) {
                    importDataAsync(uri, cb);
                    return true;
                }

            }

            //cb.onError(new Exception("Cannot import this file"));
            return false;
        }
        catch (Exception e) {
            cb.onError(e);
            return false;
        }
    }

    public void importDataAsync(final Uri uri, final CallbackSimple cb) {
        try {
            if( uri == null ) {
                cb.onError(new Exception("URI is null"));
                return;
            }
            String data = ShareUtils.getDataFromSharedFile(uri, this);
            ShareableData d = ShareUtils.decode(data);
            importDataAsync(d, cb);
        } catch (Exception e) {
            cb.onError(e);
        }
    }

    public void importDataAsync(final String data, final CallbackSimple cb) {
        try {
            ShareableData d = ShareUtils.decode(data);
            importDataAsync(d, cb);
        } catch (Exception e) {
            cb.onError(e);
        }
    }

    public void importDataAsync(final ShareableData data, final CallbackSimple cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    importData(data);
                    if (cb != null)
                        cb.onSuccess();
                } catch (Exception err) {
                    if (cb != null)
                        cb.onError(err);
                }
            }
        });

    }

    //==============================================
    //  Export Data
    //==============================================

    public ShareableData getDataToExport() {
        List<Item> items = getItems();
        List<ShopItem> shopItems = getShopItems();
        return new ShareableData(items, shopItems);
    }

    public ShareableData getShopRunData() {
        List<ShopItem> shopItems = getShopItems();
        ArrayList<Item> usedItems = new ArrayList<>();

        // Export only the used items, not all
        HashSet<String> keys = new HashSet<>();

        for (ShopItem shopItem : shopItems) {

            if (!keys.contains(shopItem.item_id)) {
                keys.add(shopItem.item_id);
                usedItems.add(shopItem.item);
            }
        }


        ShareableData data = new ShareableData(usedItems, shopItems);
        return data;
    }

    public void getShopRunDataAsync(@NonNull final Callback<ShareableData> cb ) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ShareableData data = getShopRunData();
                    cb.onSuccess(data);
                }
                catch( Exception e ) {
                    cb.onError(e);
                }

            }
        });
    }

    final static private String[] export_papable_folders = new String[] {
        Environment.DIRECTORY_DOCUMENTS,
                Environment.DIRECTORY_DOWNLOADS
    };


    public void exportData(@NonNull final ShopApplication.Callback<File> cb) {

        ShareableData data = getDataToExport();

        try {
            String json = ShareUtils.encode(data);

            File saveFolder = null;
            for( String folder_name : export_papable_folders ) {
                saveFolder = getExternalStoragePublicDirectory(folder_name);

                if (saveFolder.exists() && saveFolder.canWrite())
                    break;
            }

            if( saveFolder == null ) {
                cb.onError(new Exception("Cannot find a place where save the data :-("));
                return;
            }

            File file = new File(saveFolder, FileUtils.getDateTimePrefix() + "_shopaholic.json");

            FileUtils.writeStringToFile(file.getAbsolutePath(), json, getApplicationContext());

            cb.onSuccess(file);
        } catch (Exception e) {
            e.printStackTrace();
            cb.onError(e);
        }

    }

    public void exportDataAsync(@NonNull final ShopApplication.Callback<File> cb) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                exportData(cb);
            }
        });
    }

}
