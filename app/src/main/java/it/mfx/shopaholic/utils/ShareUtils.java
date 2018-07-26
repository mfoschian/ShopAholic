package it.mfx.shopaholic.utils;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShopItem;

public class ShareUtils {

    private static JSONObject shopItem2json(ShopItem arg) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("sid", arg.sid);
            obj.put("item_id", arg.item_id);
            obj.put("qty", arg.qty);
            obj.put("status", arg.status);
            obj.put("job_id", arg.job_id);
        }
        catch( JSONException je ) {
            je.printStackTrace();
            return null;
        }
        return obj;
    }

    private static ShopItem json2shopItem(JSONObject arg) {
        if( arg == null )
            return null;

        ShopItem obj = new ShopItem();
        try {
            obj.sid = arg.getString("sid");

            obj.item_id = arg.getString("item_id");
            obj.qty = arg.getInt("qty");
            obj.status = arg.getInt("status");
            obj.job_id = arg.optString("job_id",null);
        }
        catch( JSONException je ) {
            je.printStackTrace();
            return null;
        }
        return obj;
    }

    private static Item json2Item(JSONObject arg) {
        if( arg == null )
            return null;

        Item obj = new Item();
        try {
            obj.id = arg.getString("id");

            obj.name = arg.getString("name");
            obj.description = arg.optString("description", null);
            obj.shopName = arg.optString("shopName", null);
            obj.zoneName = arg.optString("zoneName", null);
        }
        catch( JSONException je ) {
            je.printStackTrace();
            return null;
        }
        return obj;
    }

   private static JSONObject item2json(Item arg) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", arg.id);
            obj.put("name", arg.name);
            obj.put("description", arg.description);
            obj.put("shopName", arg.shopName);
            obj.put("zoneName", arg.zoneName);
        }
        catch( JSONException je ) {
            je.printStackTrace();
            return null;
        }
        return obj;
    }

    public static void getSharableData(@NonNull ShopApplication app, @NonNull final ShopApplication.Callback<String> cb) {
        app.getShopItemsAsync(new ShopApplication.Callback<List<ShopItem>>() {
            @Override
            public void onSuccess(List<ShopItem> shopItems) {
                HashSet<String> items = new HashSet<>();

                JSONArray jShopItems = new JSONArray();
                JSONArray jItems = new JSONArray();

                for( ShopItem shopItem: shopItems ) {

                    if( ! items.contains(shopItem.item_id) ) {
                        items.add(shopItem.item_id);
                        JSONObject jItem = item2json( shopItem.item );
                        jItems.put(jItem);
                    }

                    JSONObject jShopItem = shopItem2json(shopItem);
                    if( jShopItem != null )
                        jShopItems.put(jShopItem);

                }

                String data = null;

                JSONObject res = new JSONObject();
                try {
                    res.put("items", jItems);
                    res.put("shop_items", jShopItems);

                    data = res.toString();
                }
                catch( JSONException je ) {
                    je.printStackTrace();
                    cb.onError(je);
                }

                cb.onSuccess(data);
            }

            @Override
            public void onError(Exception e) {
                cb.onError(e);
            }
        });

    }


    public static void putSharableData(@NonNull String data, @NonNull ShopApplication app, @NonNull final ShopApplication.CallbackSimple cb) {

        try {

            JSONObject jData = new JSONObject(data);

            JSONArray jItems = jData.getJSONArray("items");

            ArrayList<Item> items = new ArrayList<>();
            for (int i=0; i < jItems.length(); i++ ) {

                JSONObject jItem = jItems.getJSONObject(i);
                Item item = json2Item(jItem);
                if( item != null )
                    items.add(item);
            }


            JSONArray jShopItems = jData.getJSONArray("shop_items");

            ArrayList<ShopItem> shopItems = new ArrayList<>();
            for (int i=0; i < jShopItems.length(); i++ ) {

                JSONObject jShopItem = jShopItems.getJSONObject(i);
                ShopItem shopItem = json2shopItem(jShopItem);
                if( shopItem != null )
                    shopItems.add(shopItem);
            }


            app.saveBulkAsync(items, shopItems, new ShopApplication.CallbackSimple() {
                @Override
                public void onSuccess() {
                    cb.onSuccess();
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    cb.onError(e);
                }
            });

        }
        catch( JSONException je ) {
            je.printStackTrace();
            cb.onError(je);
        }

    }
}
