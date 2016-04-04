package com.vipercn.viper4android_v2.misc;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class Data {

    private static final String TAG = "Data";
    public static final String OFFLINE_FILENAME = "offline.data";

    public static void saveData(Context context, LinkedList<ItemInfo> data,
            String fileName) {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            File f = new File(context.getCacheDir(), fileName);
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.flush();
        } catch (IOException e) {
            Log.e(TAG, "Exception on saving instance state", e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                // ignored, can't do anything anyway
            }
        }
    }

    //
    @SuppressWarnings("unchecked")
    public static LinkedList<ItemInfo> loadData(Context context, String fileName) {
        LinkedList<ItemInfo> data = new LinkedList<ItemInfo>();
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        try {
            File f = new File(context.getCacheDir(), fileName);
            fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);

            Object o = ois.readObject();
            if (o != null && o instanceof LinkedList<?>) {
                data = (LinkedList<ItemInfo>) o;
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Unable to load stored class", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Unexpected state file format", e);
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No state info stored");
        } catch (IOException e) {
            Log.e(TAG, "Exception on loading state", e);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                // ignored, can't do anything anyway
            }
        }
        return data;
    }
}
