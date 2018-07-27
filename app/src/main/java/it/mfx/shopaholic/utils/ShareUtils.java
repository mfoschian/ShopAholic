package it.mfx.shopaholic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShopItem;

public class ShareUtils {

    //static public final String mimeType = "application/x-shopaholic-data";


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

    public static File saveDataToSharableFile(String data, Context ctx) {

        String filename = "data_shopaholic.txt";
        File folder = ctx.getFilesDir(); // + File.separator + "shared";
        File file = null;

        FileOutputStream outputStream;

        try {
            boolean ok = folder.mkdirs();
            /*
            if( !ok ) {
                Log.e("SHARESHOPDATA","Cannot make folder "+folder.toString());
                return null;
            }
            */
            //file = new File(ctx.getCacheDir(), filename);
            file = new File( folder , filename);
            file.delete();

            outputStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            file = null;
        }

        return file;
    }

    public static void shareFile(File file, String mimeType, Activity activity) {

        if (file == null || !file.exists())
            return;

        Context ctx = activity.getApplicationContext();

        //String filePath = file.getAbsolutePath();
        //Uri fileUri = Uri.parse("file://" + filePath);
        //Uri fileUri = Uri.fromFile(file);
        Uri fileUri = FileProvider.getUriForFile(ctx,"it.mfx.shopaholic.fileprovider",file);

        /*
        Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setStream(fileUri) // uri from FileProvider
                .setType(mimeType)
                .getIntent()
                .setAction(Intent.ACTION_SEND) //Change if needed
                .setDataAndType(fileUri, mimeType)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        */

        /**/
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        //intent.setDataAndType(fileUri, mimeType);
        /**/

        /*
        PackageManager pm = activity.getPackageManager();
        if (intent.resolveActivity(pm) != null) {
            activity.startActivity(intent);
        }
        */

        activity.startActivity(Intent.createChooser(intent, "ShopList Data"));
    }


}
