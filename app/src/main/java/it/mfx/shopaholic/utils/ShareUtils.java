package it.mfx.shopaholic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShareableData;
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


    public static String encode(final ShareableData data ) throws Exception {

        // Convert items
        JSONArray jItems = new JSONArray();

        if( data.items != null ) {
            for (Item item : data.items) {
                JSONObject jItem = item2json(item);
                jItems.put(jItem);
            }
        }

        // Convert ShopItems
        JSONArray jShopItems = new JSONArray();

        if( data.shopItems != null ) {
            for (ShopItem shopItem : data.shopItems) {
                JSONObject jShopItem = shopItem2json(shopItem);
                jShopItems.put(jShopItem);
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("items", jItems);
        obj.put("shop_items", jShopItems);

        String res = obj.toString();
        return res;
    }

    public static ShareableData decode(@NonNull String data) throws Exception {

        ShareableData res = new ShareableData( new ArrayList<Item>(), new ArrayList<ShopItem>() );

        JSONObject jData = new JSONObject(data);

        JSONArray jItems = jData.optJSONArray("items");
        if( jItems != null ) {
            for (int i = 0; i < jItems.length(); i++) {

                JSONObject jItem = jItems.getJSONObject(i);
                Item item = json2Item(jItem);
                if (item != null)
                    res.items.add(item);
            }
        }


        JSONArray jShopItems = jData.optJSONArray("shop_items");

        if( jShopItems != null ) {
            for (int i = 0; i < jShopItems.length(); i++) {

                JSONObject jShopItem = jShopItems.getJSONObject(i);
                ShopItem shopItem = json2shopItem(jShopItem);
                if (shopItem != null)
                    res.shopItems.add(shopItem);
            }
        }

        return res;
    }


    public static File saveDataToSharableFile(ShareableData data, Context ctx) {
        try {
            String s = encode(data);
            return saveDataToSharableFile(s,ctx);
        }
        catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    public static File saveDataToSharableFile(String data, Context ctx) {

        String filename = "data_shopaholic.json";
        File folder = ctx.getFilesDir(); // + File.separator + "shared";
        File file = null;

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
            if( file.exists() )
                file.delete();

            FileUtils.writeStringToFile(file.getAbsolutePath(), data, ctx);
        }
        catch (Exception e) {
            e.printStackTrace();
            file = null;
        }

        return file;
    }

    public static String getDataFromSharedFile(String filePath, Context ctx) {
        String result = null;

        try {
            result = FileUtils.getStringFromFile(filePath);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getDataFromSharedFile(Uri uri, Context ctx) {
        String result = null;

        try {
            result = FileUtils.getStringFromUri(uri, ctx);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return result;
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

        /* code to try ....
        // Get all activity that listen to this intent in a list.
        PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> resInfoList = packageManager.queryIntentActivities(intent , PackageManager.MATCH_DEFAULT_ONLY);

        // Loop the activity list.
        int size = resInfoList.size();
        for(int i=0;i<size;i++)
        {
            ResolveInfo resolveInfo = resInfoList.get(i);
            // Get activity package name.
            String packageName = resolveInfo.activityInfo.packageName;

            // Grant uri permission to each activity.
            //ctx.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            ctx.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        */



        activity.startActivity(Intent.createChooser(intent, "ShopList Data"));
    }

    public static void shareTextData(String data, String mimeType, Activity activity) {
        if( data == null )
            return;


        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TEXT, data);
        //intent.setDataAndType(data, mimeType);
        /**/

        //activity.startActivity(Intent.createChooser(intent, "ShopList Data"));
        Intent shareChooser = Intent.createChooser(intent, "Share with ....");


        /* non funziona
        // Add Bluetooth Share-specific data
        // Create file with text to share
        Context ctx = activity.getApplicationContext();
        final File contentFile = saveDataToSharableFile(data, ctx);
        //Uri contentUri = Uri.fromFile(contentFile);
        Uri contentUri = FileProvider.getUriForFile(ctx,"it.mfx.shopaholic.fileprovider",contentFile);

        Bundle replacements = new Bundle();
        shareChooser.putExtra(Intent.EXTRA_REPLACEMENT_EXTRAS, replacements);

        // Create Extras Bundle just for Bluetooth Share
        Bundle bluetoothExtra = new Bundle(intent.getExtras());
        replacements.putBundle("com.android.bluetooth", bluetoothExtra);

        // Add file to Bluetooth Share's Extras
        bluetoothExtra.putParcelable(Intent.EXTRA_STREAM, contentUri);
        */

        activity.startActivity(shareChooser);

    }

    public static String getDataFromHtmlBody(String html) {
        Pattern pattern = Pattern.compile("<body>(.*?)</body>");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String json = matcher.group(1);
            return json;
        }

        return null;
    }



    //==============================================
    //  Export Data
    //==============================================
    public static void getAllData(@NonNull final ShopApplication app, @NonNull final ShopApplication.Callback<String> cb ) {
        app.getItemsAsync(new ShopApplication.Callback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {


            }


            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                cb.onError(e);
            }
        });

    }


}
