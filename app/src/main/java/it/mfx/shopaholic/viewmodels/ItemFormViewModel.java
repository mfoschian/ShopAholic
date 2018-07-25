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

    public boolean isNewItem() {
        return isNewItem;
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
}
