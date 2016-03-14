package com.nadajp.littletalkers.contentprovider;

import android.content.ContentResolver;
import android.database.Cursor;

import com.nadajp.littletalkers.database.DbContract;

/**
 * Created by nadajp on 12/10/15.
 */
public class PhraseSelection {
    private static final String LOG_TAG = "PhraseSelection";

    public int getNumberOfWords(ContentResolver resolver, int id) {
        String strId = Integer.valueOf(id).toString();
        Cursor c = resolver.query(DbContract.Words.CONTENT_URI,
                new String[] {DbContract.Words._ID},
                DbContract.Words.COLUMN_NAME_KID + " = ?",
                new String[] {strId},
                null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public int getNumberOfQAs(ContentResolver resolver, int id) {
        String strId = Integer.valueOf(id).toString();
        Cursor c = resolver.query(DbContract.Questions.CONTENT_URI,
                new String[] {DbContract.Questions._ID},
                DbContract.Questions.COLUMN_NAME_KID + " = ?",
                new String[] {strId},
                null);
        int count = c.getCount();
        c.close();
        return count;
    }
}
