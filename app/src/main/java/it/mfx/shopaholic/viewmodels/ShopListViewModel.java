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

    private MutableLiveData<List<ShopItem>> mShopItems;
    private MutableLiveData<List<Item>> mItems;

    public LiveData<List<ShopItem>> getShopItems() {
        if( mShopItems == null ) {
            mShopItems = new MutableLiveData<>();
        }
        return mShopItems;
    }
    /**/
    public void setShopItems(List<ShopItem> items) {
        if( mShopItems == null ) {
            mShopItems = new MutableLiveData<>();
        }
        mShopItems.postValue( items );
    }
    /**/

    /*
    public void setShopItems(LiveData<List<ShopItem>> items) {
            mItems = items;
    }
    */
    public LiveData<List<Item>> getItems() {
        if( mItems == null ) {
            mItems = new MutableLiveData<>();
        }
        return mItems;
    }
    /**/
    public void setItems(List<Item> items) {
        if( mItems == null ) {
            mItems = new MutableLiveData<>();
        }
        mItems.postValue( items );
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
}
