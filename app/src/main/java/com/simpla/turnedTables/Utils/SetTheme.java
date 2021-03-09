package com.simpla.turnedTables.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.simpla.turnedTables.R;

public class SetTheme {

    public static int theme(Context mContext){
        SharedPreferences settings = mContext.getApplicationContext().getSharedPreferences("PREFS_NAME",0);
        int darkModeControl = settings.getInt("darkMode",0);
        if (darkModeControl == 1){
            mContext.setTheme(R.style.LightTheme);
        } else {
            mContext.setTheme(R.style.DarkTheme);
        }
        int langControl = settings.getInt("appLang",0);
        if (langControl == 1){
            LocaleHelper.setLocale(mContext,"en");
        } else {
            LocaleHelper.setLocale(mContext,"tr");
        }
        return darkModeControl;
    }
}
