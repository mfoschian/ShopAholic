package it.mfx.shopaholic.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShopItem;

public class ShopListViewModel extends AndroidViewModel {

    private ShopApplication app;
    public ShopListViewModel(@NonNull Application application) {
        super(application);

        app = (ShopApplication)application;
    }

    private MutableLiveData<List<ShopItem>> mShopItems = new MutableLiveData<>();
    private MutableLiveData<List<Item>> mItems = new MutableLiveData<>();

    public LiveData<List<ShopItem>> getShopItems() {
        return mShopItems;
    }
    /**/
    public void setShopItems(List<ShopItem> items) {
        mShopItems.postValue( items );
    }
    /**/

    /*
    public void setShopItems(LiveData<List<ShopItem>> items) {
            mItems = items;
    }
    */
    public LiveData<List<Item>> getItems() {
        return mItems;
    }
    /**/
    public void setItems(List<Item> items) {
        mItems.postValue( items );
    }


    public void loadItems() {
        app.getAsyncItems(new ShopApplication.Callback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> result) {
                setItems(result);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void loadData() {
        app.getAsyncShopItems(new ShopApplication.Callback<List<ShopItem>>() {
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


    @Override
    protected void onCleared() {
        super.onCleared();

        List<ShopItem> sitems = mShopItems.getValue();
        if( sitems == null )
            return;

         app.asyncSaveShopItems(sitems, null);
    }
}
