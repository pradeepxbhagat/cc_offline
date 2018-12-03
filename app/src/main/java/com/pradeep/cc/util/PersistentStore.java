package com.pradeep.cc.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistentStore {
    private static final String NAME = "com.pradeep.cc.util.PersistentStore";
    private static PersistentStore persistentStore = null;
    private final static String LAST_ACCESSED_TIME_KEY = "LAST_ACCESSED_TIME_KEY";

    private PersistentStore() {
    }

    public static PersistentStore getStore() {
        if (persistentStore == null) {
            synchronized (PersistentStore.class) {
                if (persistentStore == null) {
                    persistentStore = new PersistentStore();
                }
            }
        }

        return persistentStore;
    }

    public boolean putLastAccessedTime(Context context, String timeStamp) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.edit().putString(LAST_ACCESSED_TIME_KEY, timeStamp).commit();
    }

    public String getLastAccessedTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(LAST_ACCESSED_TIME_KEY, null);
    }
}
