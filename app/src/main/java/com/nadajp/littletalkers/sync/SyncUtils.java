package com.nadajp.littletalkers.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nadajp.littlealkers.backend.littleTalkersApi.model.Kid;
import com.nadajp.littlealkers.backend.littleTalkersApi.model.UserDataWrapper;
import com.nadajp.littlealkers.backend.littleTalkersApi.model.Word;
import com.nadajp.littletalkers.database.DbContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadajp on 5/17/16.
 */
public class SyncUtils {

    public static int countGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            return 0;
        } else {
            return accounts.length;
        }
    }

    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
                                                                     final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private static ArrayList<Kid> getKids(ContentResolver resolver) {
        ArrayList<Kid> kids = new ArrayList<Kid>();
        Cursor cursor = resolver.
                query(DbContract.Kids.CONTENT_URI,
                        null,
                        DbContract.Kids.COLUMN_NAME_IS_DIRTY + " = ? ",
                        new String[]{"1"},
                        null);

        if (cursor.moveToFirst()) {
            do {
                Kid kid = new Kid();
                kid.setId(cursor.getLong(cursor.getColumnIndex(DbContract.Kids._ID)));
                kid.setName(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME)));
                kid.setBirthdate(cursor.getLong(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS)));
                kid.setLocation(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION)));
                kid.setLanguage(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE)));
                kid.setPictureUri(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI)));
                kids.add(kid);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return kids;
    }

    public static ArrayList<Word> getWords(ContentResolver resolver) {
        ArrayList<Word> words = new ArrayList<Word>();
        Cursor c = resolver.
                query(DbContract.Words.CONTENT_URI,
                        null,
                        DbContract.Words.COLUMN_NAME_IS_DIRTY + " = ? ",
                        new String[]{"1"},
                        null);

        if (c.moveToFirst()) {
            do {
                Word word = new Word();
                word.setKidId(c.getLong(c.getColumnIndex(DbContract.Words.COLUMN_NAME_KID)));
                word.setWord(c.getString(c.getColumnIndex(DbContract.Words.COLUMN_NAME_WORD)));
                word.setLanguage(c.getString(c.getColumnIndex(DbContract.Words.COLUMN_NAME_LANGUAGE)));
                word.setDate(c.getLong(c.getColumnIndex(DbContract.Words.COLUMN_NAME_DATE)));
                word.setLocation(c.getString(c.getColumnIndex(DbContract.Words.COLUMN_NAME_LOCATION)));
                word.setTranslation(c.getString(c.getColumnIndex(DbContract.Words.COLUMN_NAME_TRANSLATION)));
                word.setToWhom(c.getString(c.getColumnIndex(DbContract.Words.COLUMN_NAME_TOWHOM)));
                word.setNotes(c.getString(c.getColumnIndex(DbContract.Words.COLUMN_NAME_NOTES)));
                word.setAudioFileUri(c.getString(c.getColumnIndex(DbContract.Words.COLUMN_NAME_AUDIO_FILE)));
                words.add(word);
            } while (c.moveToNext());
        }
        c.close();
        return words;
    }

    public static UserDataWrapper getUserData(ContentResolver resolver) {
        UserDataWrapper data = new UserDataWrapper();
        data.setKids(getKids(resolver));
        data.setWords(getWords(resolver));
        return data;
    }

    public static void setNotDirty(UserDataWrapper data, ContentResolver resolver)
    {
        List<Kid> kids = data.getKids();
        List<Word> words = data.getWords();

        if (kids == null) { return; }

        ContentValues values = new ContentValues();
        values.put("is_dirty", 0);

        for (Kid kid : kids) {
            resolver.update(DbContract.Kids.CONTENT_URI, values, DbContract.Kids._ID + "=?",
                    new String[] { (kid.getId()).toString() });
        }
        if (words == null) { return; }
        for (Word word : words) {
            resolver.update(DbContract.Words.CONTENT_URI, values, DbContract.Words.COLUMN_NAME_WORD + "=?",
                    new String[] {word.getWord()});
        }
    }

}
