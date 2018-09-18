package com.gcrj.projectcontrol.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gcrj.projectcontrol.BaseApplication;


public class SharedPreferenceUtils {

    public static void setString(String key, String value) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static void setBoolean(String key, boolean value) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static String getString(String key) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static String getString(String key, String value) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, value);
    }

    public static boolean getBoolean(String key, boolean flag) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, flag);
    }

    public static void clear(String key) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit().remove(key);
        edit.apply();
    }

    public static void setInt(String key, int value) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static int getInt(String key) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static int getInt(String key, int f) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, f);
    }

    public static void setLong(String key, long value) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public static long getLong(String key) {
        SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void clear(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit().remove(key);
        edit.apply();
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static void setLong(Context context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

    public static long getLong(Context context, String key, long f) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, f);
    }
}
