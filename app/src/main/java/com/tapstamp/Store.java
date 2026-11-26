package com.tapstamp;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Saves tap timestamps (epoch millis) in SharedPreferences, oldest first. */
final class Store {
    private static final String PREFS = "tapstamp";
    private static final String KEY = "taps";

    private Store() {}

    static synchronized void add(Context c, long millis) {
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String cur = p.getString(KEY, "");
        String next = cur.isEmpty() ? String.valueOf(millis) : cur + "," + millis;
        p.edit().putString(KEY, next).apply();
    }

    static synchronized List<Long> all(Context c) {
        String cur = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "");
        List<Long> out = new ArrayList<>();
        if (cur.isEmpty()) return out;
        for (String s : cur.split(",")) {
            try { out.add(Long.parseLong(s)); } catch (NumberFormatException ignored) {}
        }
        return out;
    }

    static synchronized void clear(Context c) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY).apply();
    }

    /** e.g. "Thu 26 Nov 2026  01:22:45.123" in the phone's current time zone. */
    static String full(long millis) {
        return new SimpleDateFormat("EEE dd MMM yyyy  HH:mm:ss.SSS", Locale.getDefault())
                .format(new Date(millis));
    }

    static String time(long millis) {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(millis));
    }

    static String millisPart(long millis) {
        return new SimpleDateFormat(".SSS", Locale.getDefault()).format(new Date(millis));
    }

    static String date(long millis) {
        return new SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault()).format(new Date(millis));
    }
}
