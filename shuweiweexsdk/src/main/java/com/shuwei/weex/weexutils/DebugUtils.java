package com.shuwei.weex.weexutils;

import android.content.Context;
import android.preference.PreferenceManager;

public class DebugUtils {

    private static final String KEY_DEBUG_SERVER = "key_debug_server";
    private static final String KEY_DEBUG_HOST = "key_debug_host";
    private static final String KEY_IS_SERVER = "key_is_server";

    public static void setDebugServer(Context context, String address) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(KEY_DEBUG_SERVER, address)
            .apply();
    }

    public static void setDebugHost(Context context, String host) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(KEY_DEBUG_HOST, host)
            .apply();
    }

    public static String getDebugServer(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(KEY_DEBUG_SERVER, null);
    }

    public static String getDebugHost(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(KEY_DEBUG_HOST, null);
    }

    public static void setIsServer(Context context, boolean isServer) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_IS_SERVER, isServer)
            .apply();
    }

    public static boolean getIsServer(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_IS_SERVER, false);
    }
}
