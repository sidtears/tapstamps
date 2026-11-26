package com.tapstamp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;

public class StampWidget extends AppWidgetProvider {

    static final String ACTION_STAMP = "com.tapstamp.ACTION_STAMP";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_STAMP.equals(intent.getAction())) {
            // Grab the time first, before anything else runs.
            long now = System.currentTimeMillis();
            Store.add(context, now);
            Toast.makeText(context, "Saved " + Store.time(now) + Store.millisPart(now),
                    Toast.LENGTH_SHORT).show();
            refreshAll(context);
            return;
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            mgr.updateAppWidget(id, build(context));
        }
    }

    static void refreshAll(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(context, StampWidget.class));
        for (int id : ids) {
            mgr.updateAppWidget(id, build(context));
        }
    }

    private static RemoteViews build(Context context) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

        List<Long> taps = Store.all(context);
        if (taps.isEmpty()) {
            rv.setTextViewText(R.id.time, "--:--:--");
            rv.setTextViewText(R.id.date, "No taps yet");
        } else {
            long last = taps.get(taps.size() - 1);
            rv.setTextViewText(R.id.time, Store.time(last) + Store.millisPart(last));
            rv.setTextViewText(R.id.date, Store.date(last));
        }
        rv.setTextViewText(R.id.hint, "TAP TO STAMP  ·  " + taps.size() + " saved");

        Intent i = new Intent(context, StampWidget.class).setAction(ACTION_STAMP);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        rv.setOnClickPendingIntent(R.id.root, pi);

        // Small "open history" button.
        Intent open = new Intent(context, MainActivity.class);
        PendingIntent openPi = PendingIntent.getActivity(context, 1, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        rv.setOnClickPendingIntent(R.id.history, openPi);
        return rv;
    }
}
