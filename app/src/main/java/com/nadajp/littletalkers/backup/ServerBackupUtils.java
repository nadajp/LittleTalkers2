package com.nadajp.littletalkers.backup;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.database.DbContract.Words;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.server.littletalkersapi.model.Kid;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserDataWrapper;
import com.nadajp.littletalkers.server.littletalkersapi.model.Word;


public class ServerBackupUtils
{
   
   private static final String DEBUG_TAG = "DatabaseBackupUtils";
   
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

   private static ArrayList<Kid> getKids()
   {
      ArrayList<Kid> kids = new ArrayList<Kid>();
      Cursor cursor = DbSingleton.get().getKidsForSync();
      
      if (cursor.moveToFirst())
      {
         do
         {  
            Kid kid = new Kid();
            kid.setId(cursor.getLong(cursor.getColumnIndex(Kids._ID)));
            kid.setName(cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_NAME)));
            kid.setBirthdate(cursor.getLong(cursor.getColumnIndex(Kids.COLUMN_NAME_BIRTHDATE_MILLIS)));
            kid.setLocation(cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_DEFAULT_LOCATION)));
            kid.setLanguage(cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_DEFAULT_LANGUAGE)));
            kid.setPictureUri(cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_PICTURE_URI)));         
            kids.add(kid);
            
         } while (cursor.moveToNext());
      }
      cursor.close();
      return kids;
   }
   
   public static ArrayList<Word> getWords()
   {
      ArrayList<Word> words = new ArrayList<Word>();
      Cursor c = DbSingleton.get().getWordsForSync();
      if (c.moveToFirst())
      {
         do
         {  
            Word word = new Word();
            word.setKidId(c.getLong(c.getColumnIndex(Words.COLUMN_NAME_KID)));
            word.setWord(c.getString(c.getColumnIndex(Words.COLUMN_NAME_WORD)));
            word.setLanguage(c.getString(c.getColumnIndex(Words.COLUMN_NAME_LANGUAGE)));
            word.setDate(c.getLong(c.getColumnIndex(Words.COLUMN_NAME_DATE)));
            word.setLocation(c.getString(c.getColumnIndex(Words.COLUMN_NAME_LOCATION)));
            word.setTranslation(c.getString(c.getColumnIndex(Words.COLUMN_NAME_TRANSLATION)));
            word.setToWhom(c.getString(c.getColumnIndex(Words.COLUMN_NAME_TOWHOM)));
            word.setNotes(c.getString(c.getColumnIndex(Words.COLUMN_NAME_NOTES)));
            word.setAudioFileUri(c.getString(c.getColumnIndex(Words.COLUMN_NAME_AUDIO_FILE)));
            words.add(word);
         } while (c.moveToNext());
      }
      c.close();
      return words;
   }
   
   public static UserDataWrapper getUserData()
   {
      UserDataWrapper data = new UserDataWrapper();
      data.setKids(getKids());
      data.setWords(getWords());
      return data;
   }

}
