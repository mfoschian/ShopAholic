package it.mfx.shopaholic.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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

    private void setShopItems(List<ShopItem> items) {
        mShopItems.postValue( items );
    }

    public LiveData<List<Item>> getItems() {
        return mItems;
    }

    public void setItems(List<Item> items) {
        mItems.postValue( items );
    }

    public void addItem(Item item) {
        List<Item> items = mItems.getValue();
        items.add(item);
        mItems.postValue( items );
    }

    public void loadItems() {
        app.getItemsAsync(new ShopApplication.Callback<List<Item>>() {
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

    public void loadItemsSync() {
        List<Item> items = app.getItems();
        setItems(items);
    }

    public void filterItems( String filter ) {
        if( filter == null || "".equals(filter)) {
            loadItems();
        }
        else {
            String pattern = "%" + filter + "%";
            app.getItemsByNameAsync(pattern, new ShopApplication.Callback<List<Item>>() {
                @Override
                public void onSuccess(List<Item> result) {
                    setItems(result);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
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

    public ShopItem findByItemId(String item_id) {
        List<ShopItem> items = mShopItems.getValue();
        if( items == null )
            return null;

        for( ShopItem shopItem : items ) {
            if( item_id.equals( shopItem.item_id ) )
                return shopItem;
        }
        return null;
    }

    public void addShopItem(ShopItem shopItem, ShopApplication.Callback<Boolean> cb ) {
        app.addShopItemAsync(shopItem, cb);
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
