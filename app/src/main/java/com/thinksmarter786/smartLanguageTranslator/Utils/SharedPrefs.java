package com.thinksmarter786.smartLanguageTranslator.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;
import java.util.Set;

public class SharedPrefs {
    public static String PREFERENCE = "CallRecording";

    public static String initialStart = "onBoardStart";
    private static SharedPrefs instance;
    private SharedPreferences sharedPreferences;
    private  Context ctx;
    public SharedPrefs(Context context) {
        ctx = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCE, 0);
    }

    public static SharedPrefs getInstance(Context ctx) {
        if (instance == null) {
            instance = new SharedPrefs(ctx);
        }
        return instance;
    }

    public void putBoolean(String key, Boolean val) {
        sharedPreferences.edit().putBoolean(key, val).apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
    public void putString(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    public String getString(String key) {
        String lng = Locale.getDefault().getDisplayName();
        String defLng="";
        if(lng.indexOf("(") > -1)
            defLng = lng.substring(0,lng.indexOf("(")).trim();
        else
            defLng = lng;
        return sharedPreferences.getString(key, defLng);
    }
    public String getCode(String key) {
        return sharedPreferences.getString(key, Locale.getDefault().getLanguage());
    }

    public void putObject(String key, Set<String> speechLanguageDataModels){
        sharedPreferences.edit().putStringSet(key, speechLanguageDataModels).apply();
    }
    public Set<String> getObject(String key){
        return sharedPreferences.getStringSet(key, null);
    }

}
