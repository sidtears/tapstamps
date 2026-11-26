package com.tapstamp;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** History screen: shows every saved tap, newest first. Also has a manual stamp button. */
public class MainActivity extends Activity {

    private ArrayAdapter<String> adapter;
    private final List<String> rows = new ArrayList<>();
    private TextView count;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);

        TextView hint = new TextView(this);
        hint.setText("Long-press your home screen → Widgets → TapStamp to add the widget.");
        hint.setTextSize(13);
        root.addView(hint);

        Button stamp = new Button(this);
        stamp.setText("Stamp now");
        stamp.setOnClickListener(v -> {
            Store.add(this, System.currentTimeMillis());
            StampWidget.refreshAll(this);
            reload();
        });
        root.addView(stamp);

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);

        Button copy = new Button(this);
        copy.setText("Copy all");
        copy.setOnClickListener(v -> copyAll());
        bar.addView(copy);

        Button clear = new Button(this);
        clear.setText("Clear");
        clear.setOnClickListener(v -> {
            Store.clear(this);
            StampWidget.refreshAll(this);
            reload();
        });
        bar.addView(clear);
        root.addView(bar);

        count = new TextView(this);
        count.setTextSize(14);
        count.setPadding(0, pad / 2, 0, pad / 2);
        root.addView(count);

        ListView list = new ListView(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rows);
        list.setAdapter(adapter);
        root.addView(list, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        List<Long> taps = Store.all(this);
        rows.clear();
        int n = taps.size();
        for (int i = n - 1; i >= 0; i--) {
            rows.add("#" + (i + 1) + "   " + Store.full(taps.get(i)));
        }
        adapter.notifyDataSetChanged();
        count.setText(n + (n == 1 ? " tap saved" : " taps saved"));
    }

    private void copyAll() {
        List<Long> taps = Store.all(this);
        List<String> lines = new ArrayList<>();
        for (Long t : taps) lines.add(Store.full(t));
        Collections.reverse(lines);
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("TapStamp", String.join("\n", lines)));
        Toast.makeText(this, "Copied " + lines.size() + " timestamps", Toast.LENGTH_SHORT).show();
    }
}
