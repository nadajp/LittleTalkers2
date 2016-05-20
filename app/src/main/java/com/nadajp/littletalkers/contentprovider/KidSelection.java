package com.nadajp.littletalkers.contentprovider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;

import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.model.Kid;

/**
 * Created by nadajp on 12/7/15.
 */
public class KidSelection {

    private static final String LOG_TAG = "KidSelection";

    private static final String[] BASIC_INFO_PROJECTION =
            {Kids.COLUMN_NAME_NAME,
                    Kids.COLUMN_NAME_DEFAULT_LOCATION,
                    Kids.COLUMN_NAME_DEFAULT_LANGUAGE};

    public Kid getKidInfo(ContentResolver resolver, int id) {
        String strId = Integer.valueOf(id).toString();
        Cursor c = resolver.query(Kids.buildKidsUri(id),
                BASIC_INFO_PROJECTION,
                null,
                null,
                null);

        if (c.getCount() == 0) {
            Log.i(LOG_TAG, "No kid found with id " + id);
            Log.i(LOG_TAG, Kids.buildKidsUri(id).toString());
            return null;
        }
        Kid kid = new Kid(id, c.getString(c.getColumnIndex(Kids.COLUMN_NAME_NAME)),
                c.getString(c.getColumnIndex(Kids.COLUMN_NAME_DEFAULT_LOCATION)),
                c.getString(c.getColumnIndex(Kids.COLUMN_NAME_DEFAULT_LANGUAGE)));

        return kid;
    }
}
