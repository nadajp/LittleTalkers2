package com.nadajp.littletalkers.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.ui.AddItemActivity;
import com.nadajp.littletalkers.utils.Prefs;

/**
 * Created by nadajp on 5/25/16.
 */
public class LittleTalkersWidgetProvider extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {

            // Create an Intent to launch AddItemActivity
            Intent intentWord = new Intent(context, AddItemActivity.class);
            intentWord.putExtra(Prefs.TYPE, Prefs.TYPE_WORD);
            PendingIntent addWordPendingIntent = PendingIntent.getActivity(context, 0, intentWord, 0);

            Intent intentQA = new Intent(context, AddItemActivity.class);
            intentQA.putExtra(Prefs.TYPE, Prefs.TYPE_QA);
            PendingIntent addQAPendingIntent = PendingIntent.getActivity(context, 1, intentQA, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lt_widget);
            views.setOnClickPendingIntent(R.id.button_add_word, addWordPendingIntent);
            views.setOnClickPendingIntent(R.id.button_add_qa, addQAPendingIntent);
            //views.setTextViewText(R.id.kid_name, "Leo");

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
