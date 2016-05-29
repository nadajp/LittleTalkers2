package com.nadajp.littletalkers.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.ui.MainActivity;
import com.nadajp.littletalkers.utils.Prefs;

/**
 * Created by nadajp on 5/25/16.
 */
public class LittleTalkersWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lt_widget);

            // Differentiaing between intents by setting different actions and data is
            // the only way to get the extras to be passed in correctly
            Intent intentWord = new Intent().setClass(context, MainActivity.class);
            intentWord.setAction(Long.toString(System.currentTimeMillis()));
            intentWord.setData(Uri.parse("wordtype"));
            intentWord.putExtra(Prefs.TYPE, Prefs.TYPE_WORD);
            PendingIntent addWordPendingIntent = PendingIntent.getActivity(context, 0, intentWord, 0);
            views.setOnClickPendingIntent(R.id.button_add_word, addWordPendingIntent);

            Intent intentQA = new Intent().setClass(context, MainActivity.class);
            intentQA.setAction(Long.toString(System.currentTimeMillis()));
            intentQA.setData(Uri.parse("qatype"));
            intentQA.putExtra(Prefs.TYPE, Prefs.TYPE_QA);
            PendingIntent addQAPendingIntent = PendingIntent.getActivity(context, 1, intentQA, 0);
            views.setOnClickPendingIntent(R.id.button_add_qa, addQAPendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
