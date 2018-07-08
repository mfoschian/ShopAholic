package it.mfx.shopaholic.models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import it.mfx.shopaholic.ShopApplication;

public class ShopListViewModel extends ViewModel {


    private MutableLiveData<List<ShopItem>> mItems;

    public LiveData<List<ShopItem>> getShopItems() {
        if( mItems == null ) {
            mItems = new MutableLiveData<>();
        }
        return mItems;
    }
    /**/
    public void setShopItems(List<ShopItem> items) {
        if( mItems == null ) {
            mItems = new MutableLiveData<>();
        }
        mItems.postValue( items );
    }
    /**/

    /*
    public void setShopItems(LiveData<List<ShopItem>> items) {
            mItems = items;
    }
    */


}
