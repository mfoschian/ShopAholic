package it.mfx.shopaholic.models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import it.mfx.shopaholic.ShopApplication;

public class ShopListViewModel extends ViewModel {


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

}
