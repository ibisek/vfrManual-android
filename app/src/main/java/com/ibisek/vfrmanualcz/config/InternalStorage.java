package com.ibisek.vfrmanualcz.config;

/**
 * Recycled by ibisek on 20.3.18 from Outlanded.
 */

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

public class InternalStorage<T> {

    private Context context;
    private String filename;

    public InternalStorage(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    /**
     * @return object from internal storage or null in case of exception
     */
    @SuppressWarnings("unchecked")
    public T load() {
        T object = null;

        try {
            ObjectInputStream ois = new ObjectInputStream(context.openFileInput(filename));
            object = (T) ois.readObject();

        } catch (Exception ex) {
            Log.e("UFON", "Error when loading file '"+filename+"': "+ex.getMessage());
        }

        return object;
    }

    /**
     * @param filename
     * @param object
     *          MUST BE SERIALIZABLE (!)
     */
    public void save(T object) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            oos.close();

        } catch (Exception ex) {
            Log.e("UFON", "Error when saving file '"+filename+"': "+ex.getMessage());
        }
    }

}
