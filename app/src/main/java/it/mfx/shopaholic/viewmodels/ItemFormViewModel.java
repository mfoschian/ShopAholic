package it.mfx.shopaholic.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import it.mfx.shopaholic.models.Item;

public class ItemFormViewModel extends ViewModel {

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
}
