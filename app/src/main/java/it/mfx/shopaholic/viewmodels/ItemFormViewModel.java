package it.mfx.shopaholic.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import java.util.List;

import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.models.Item;

public class ItemFormViewModel extends AndroidViewModel {

    private ShopApplication app;
    public ItemFormViewModel(@NonNull Application application) {
        super(application);

        app = (ShopApplication)application;
    }

    private MutableLiveData<Item> mItem = new MutableLiveData<>();
    private boolean isNewItem = false;

    public LiveData<Item> getItem() {
        return mItem;
    }

    public void setExistingItem( Item item ) {
        isNewItem = false;
        mItem.postValue(item);
    }

    public void setNewItem( Item item ) {
        isNewItem = true;
        mItem.postValue(item);
    }

    public void setItemDeleted() {
        isNewItem = false;
        mItem.postValue(null);
    }

    public boolean isNewItem() {
        return isNewItem;
    }

    public boolean deleteItem() {
        Item item = mItem.getValue();
        if( item == null ) {
            return true;
        }

        boolean can_delete = app.isItemDeletable(item.id);
        if( !can_delete )
            return false;

        app.deleteItem(item);
        mItem.postValue(null);

        return true;
    }


    public void deleteItemAsync(@NonNull final ShopApplication.Callback<Boolean> cb ) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if( deleteItem() )
                        cb.onSuccess(true);
                    else
                        cb.onSuccess(false);
                }
                catch (Exception err) {
                    cb.onError(err);
                }
            }
        });
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
        app.getShopNamesAsync(new ShopApplication.Callback<List<String>>() {
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
    // ZoneNames
    //==================================
    private MutableLiveData<List<String>> zoneNames = new MutableLiveData<>();

    public LiveData<List<String>> getZoneNames() { return zoneNames; }

    private void setZoneNames( List<String> zoneNames ) {
        this.zoneNames.postValue(zoneNames);
    }

    public void loadZoneNames(String shopName) {
        app.getShopZoneNamesAsync(shopName, new ShopApplication.Callback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                setZoneNames(result);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
/*
    private String currentShopName;

    public void notifyShopNameChanged(String name) {
        currentShopName = name;

    }
*/

}
