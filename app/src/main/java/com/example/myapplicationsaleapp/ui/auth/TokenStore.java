package com.example.myapplicationsaleapp.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenStore {
    private static final String SP_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "jwt";

    public static void save(Context ctx, String token) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_TOKEN, token).apply();
    }

    public static String get(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_TOKEN, null);
    }

    public static void clear(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(KEY_TOKEN).apply();
    }
}
