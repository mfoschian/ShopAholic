package it.mfx.shopaholic.utils;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    //
    // READ
    //
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        //FileInputStream fin = ctx.openFileInput(filePath);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String getStringFromUri( Uri uri, Context ctx) throws Exception {
        InputStream fin = ctx.getContentResolver().openInputStream(uri);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    //
    // WRITE
    //
    public static void writeStringToStream(FileOutputStream outputStream, String data) throws Exception {

        try {
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public static void writeStringToFile(String filePath, String data, Context ctx) throws Exception {

        try {
            //FileOutputStream outputStream = ctx.openFileOutput(filePath, Context.MODE_PRIVATE);
            File fl = new File(filePath);
            FileOutputStream outputStream = new FileOutputStream(fl);
            writeStringToStream(outputStream, data);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    //
    // MISC
    //
    public static String getDateTimePrefix() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALIAN);
        Date now = new Date();
        String fileName = formatter.format(now);
        return fileName;
    }
}
