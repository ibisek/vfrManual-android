package com.ibisek.vfrmanualcz.config;

import android.content.Context;
import android.util.Log;

import com.ibisek.vfrmanualcz.data.AirportRecord;
import com.ibisek.vfrmanualcz.data.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Recycled by ibisek on 20.3.18 from Outlanded.
 */

public class Configuration {

    private static final String TAG = Configuration.class.getName();

    private static final String CONFIGURATION_FILENAME = "configuration.bin";

    // last recently used contacts:
    public final static String LRU_SEARCH_RESULTS = "lruContacts";
    public final static int LRU_LIST_MAX_LEN = 10;

    private static Configuration instance;

    private InternalStorage<Properties> storage;
    private Properties properties;

    private Context context;
    private boolean dirty;

    /**
     * @param context
     */
    private Configuration(Context context) {
        this.context = context;

        Log.d(TAG, "CONFIGURATION_FILENAME: " + CONFIGURATION_FILENAME);
        Log.d(TAG, "LRU_LIST_MAX_LEN: " + LRU_LIST_MAX_LEN);

        storage = new InternalStorage<Properties>(CONFIGURATION_FILENAME, context);

        properties = storage.load();
        if (properties == null)
            properties = createDefaultProperties();
    }

    /**
     * @param context needs to be created just once, then can be called with null. It can be also given by calling getApplicationContext().
     * @return instance of {@link Configuration} or null in case given context is null and getInstace has been called for the first time
     */
    public static Configuration getInstance(Context context) {
        if (instance == null && context == null)
            return null;

        if (instance == null)
            instance = new Configuration(context);

        return instance;
    }

    /**
     * @return default set of properties
     */
    private Properties createDefaultProperties() {
        Properties props = new Properties();

        return props;
    }

    /**
     * Saves configuration into local storage.
     */
    public void save() {
        if (dirty)
            storage.save(properties);
    }

    private static final String CSV_SEPARATOR = "|";

    /**
     * @return list of recently used search results
     */
    public List<AirportRecord> getLastRecentlyUsedSearchResults() {
        List<AirportRecord> list = new ArrayList<>();

        if (properties.containsKey(LRU_SEARCH_RESULTS)) {
            String csv = properties.getProperty(LRU_SEARCH_RESULTS);

            StringTokenizer st = new StringTokenizer(csv, CSV_SEPARATOR);
            while (st.hasMoreElements()) {
                String code = st.nextToken();

                AirportRecord rec = DataRepository.getInstance(context).findByCodeOrAlias(code);
                list.add(rec);
            }
        }

        return list;
    }

    public void saveLastRecentlyUsedSearchResults(List<AirportRecord> records) {
        if (records.size() > 0) {
            StringBuilder csv = new StringBuilder();

            int i = 0;
            for (AirportRecord rec : records) {
                csv.append(rec.code);
                csv.append(CSV_SEPARATOR);

                if (++i == LRU_LIST_MAX_LEN)
                    break; // store no more entries than MAX
            }

            properties.put(LRU_SEARCH_RESULTS, csv.toString());
            dirty = true;
        }
    }
}