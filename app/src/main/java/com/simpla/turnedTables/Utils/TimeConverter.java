package com.simpla.turnedTables.Utils;

import android.content.Context;

import com.simpla.turnedTables.R;

public class TimeConverter {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String convert(long time, Context context){
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return context.getResources().getString(R.string.now);
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getResources().getString(R.string.justNow);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.oneM);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + context.getResources().getString(R.string.m);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.oneH);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + context.getResources().getString(R.string.h);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getResources().getString(R.string.yesterday);
        } else {
            return diff / DAY_MILLIS + context.getResources().getString(R.string.d);
        }
    }

}
