package it.mfx.shopaholic.models;

import java.util.List;

public class ShareableData {
    public List<Item> items;
    public List<ShopItem> shopItems;

    public ShareableData() {
        this.items = null;
        this.shopItems = null;
    }
    public ShareableData(List<Item> items, List<ShopItem> shopItems) {
        this.items = items;
        this.shopItems = shopItems;
    }
}

