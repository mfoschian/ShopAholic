package it.mfx.shopaholic.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.models.ShopItem;

public class ShopRunViewModel  extends AndroidViewModel {

    private ShopApplication app;
    public ShopRunViewModel(@NonNull Application application) {
        super(application);

        app = (ShopApplication)application;
    }

    //==================================
    // ShopItems
    //==================================

    private MutableLiveData<List<ShopItem>> mShopItems = new MutableLiveData<>();

    public LiveData<List<ShopItem>> getShopItems() {
        return mShopItems;
    }

    private void setShopItems(List<ShopItem> items) {
        mShopItems.postValue( items );
    }

    public void loadShopItems() {
        app.getShopItemsAsync(new ShopApplication.Callback<List<ShopItem>>() {
            @Override
            public void onSuccess(List<ShopItem> result) {
                setShopItems(result);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadShopItemsSync() {
        List<ShopItem> items = app.getShopItems();
        setShopItems(items);
    }

    //==================================
    // ShopNames
    //==================================
    private MutableLiveData<List<String>> shopNames = new MutableLiveData<>();

    public LiveData<List<String>> getShopNames() { return shopNames; }

    private void setShopNames( List<String> shopNames ) {
        this.shopNames.postValue(shopNames);
    }

    public void loadShopNames() {
        app.getActiveShopNamesAsync(new ShopApplication.Callback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                setShopNames(result);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    //==================================
    // Done Visibility
    //==================================
    static final private boolean default_show_items_done = true;
    private boolean showItemsDone = default_show_items_done;

    public void setShowItemsDone(boolean flag) {
        this.showItemsDone = flag;
    }

    public boolean isShowingItemsDone() {
        return showItemsDone;
    }

    private void setItemStatus(String shop_item_id, int status, ShopApplication.Callback<Boolean> cb ) {
        List<ShopItem> items = mShopItems.getValue();
        if( items == null ) {
            cb.onSuccess(true);
            return;
        }
        for( ShopItem item: items ) {
            if( item.sid.equals(shop_item_id)) {
                item.status = status;
                app.saveShopItemAsync(item, cb);
                break;
            }
        }
    }

    public void setItemDone( String shop_item_id, ShopApplication.Callback<Boolean> cb ) {
        setItemStatus(shop_item_id, ShopItem.Status.DONE, cb);
    }

    public void setItemUnDone( String shop_item_id, ShopApplication.Callback<Boolean> cb ) {
        setItemStatus(shop_item_id, ShopItem.Status.PENDING, cb);
    }

    public void revertItemStatus(String shop_item_id, ShopApplication.Callback<Boolean> cb ) {
        List<ShopItem> items = mShopItems.getValue();
        if( items == null ) {
            cb.onSuccess(true);
            return;
        }
        for( ShopItem item: items ) {
            if( item.sid.equals(shop_item_id)) {
                if( item.status == ShopItem.Status.DONE )
                    item.status = ShopItem.Status.PENDING;
                else
                    item.status = ShopItem.Status.DONE;

                app.saveShopItemAsync(item, cb);
                break;
            }
        }
    }


    //==================================
    // Save actions
    //==================================
    public void saveChanges(ShopApplication.Callback<Integer> cb) {
        List<ShopItem> items = getShopItems().getValue();
        app.saveShopItemsAsync(items, cb);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        List<ShopItem> sitems = mShopItems.getValue();
        if( sitems == null )
            return;

        app.saveShopItemsAsync(sitems, null);
    }
}
