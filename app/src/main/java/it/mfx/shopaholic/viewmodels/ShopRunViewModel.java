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

    private MutableLiveData<List<ShopItem>> mShopItems = new MutableLiveData<>();

    public LiveData<List<ShopItem>> getShopItems() {
        return mShopItems;
    }

    private void setShopItems(List<ShopItem> items) {
        mShopItems.postValue( items );
    }

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
