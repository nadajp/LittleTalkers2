package com.nadajp.littletalkers.utils;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs
{
   public static final String SHARED_PREFS_FILENAME = "com.nadajp.littletalkers.shared_prefs";
   public static final String ACCOUNT_NAME = "account_name";
   public static final String CURRENT_KID_ID = "current_kid_id";
   public static final String PROFILE_PIC_PATH = "profile_picture_path";
   public static final String LANGUAGE_FILTER = "language_filter";
   public static final String SORT_ASCENDING = "sort_ascending";
   public static final String SORT_COLUMN = "sort_column";
   public static final String SORT_COLUMN_ID = "sort_column_id";
   public static final String ITEM_ID = "item_id";
   public static final String POSITION = "position";
   public static final String AUDIO_RECORDED = "audio_recorded";
   public static final String AUDIO_FILE = "audio_file";
   public static final String ADD_TYPE = "add_type";
   public static final String FRAGMENT_LAYOUT = "fragment_layout";
   public static final String HEADER_LAYOUT = "header_layout";
   public static final String ROW_LAYOUT = "row_layout";
   public static final String PHRASE_HEADER_ID = "phrase_header_id";
   public static final String PHRASE_COLUMN_NAME = "phrase_column_name";
   public static final String LIST_TYPE = "list_type";
   public static final String TYPE = "type";
   public static final String TAB_ID = "tab_id";
   public static final String SECOND_RECORDING = "second_recording";
   public static final String KIDS_CHANGED = "kids_changed";
   public static final String SHOWING_MORE_FIELDS = "more_fields";
   public static final String PHRASE_ENTERED = "phrase_entered";
   public static final String TEMP_FILE_STEM = "temp_file_stem";
   public static final String USER_ID = "user_id";
   public static final String LOGGED_IN = "logged_in";
   public static final String UPGRADED = "upgraded";

   public static final String PHRASE = "phrase";
   public static final String ANSWER = "answer";
   public static final String DATE = "date";
   public static final String LOCATION = "location";
   public static final String TRANSLATION = "translation";
   public static final String TO_WHOM = "to_whom";
   public static final String NOTES = "notes";

   public static final int EDIT_KID = 0;

   public static final int TYPE_WORD = 0;
   public static final int TYPE_QA = 1;

   public static final int SORT_COLUMN_PHRASE = 0;
   public static final int SORT_COLUMN_DATE = 1;
   
   public static final String UPDATE_AVAILABLE = "update_available";

   public static Boolean getIsUpdateAvailable(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getBoolean(UPDATE_AVAILABLE, false);
   }
   
   public static void setIsUpdateAvailable(Context context, Boolean isAvailable)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putBoolean(UPDATE_AVAILABLE, isAvailable);
      editor.commit();
   }
   
   public static String getAccountName(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getString(ACCOUNT_NAME, null);
   }
   
   public static void saveAccountName(Context context, String accountName)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putString(ACCOUNT_NAME, accountName);
      editor.commit();
   }
   
   public static Boolean getIsLoggedIn(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getBoolean(LOGGED_IN, false);      
   }
   
   public static Boolean getUpgraded(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getBoolean(UPGRADED, false);      
   }
   
   public static Long getUserId(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getLong(USER_ID, -1);
   }
   
   public static void saveUserId(Context context, Long id)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putLong(USER_ID, id);
      editor.commit();
   }
   
   public static int getKidId(Context context, int defaultId)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getInt(CURRENT_KID_ID, defaultId);
   }

   public static void saveKidId(Context context, int id)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putInt(CURRENT_KID_ID, id);
      editor.commit();
   }

   public static int getType(Context context, int defaultType)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getInt(TYPE, defaultType);
   }

   public static void saveType(Context context, int type)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putInt(TYPE, type);
      editor.commit();
   }

   public static void savePhraseInfo(Context context, long date, String phrase,
         String location, String translation, String toWhom, String notes,
         String audioFile)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putLong(DATE, date);
      editor.putString(PHRASE, phrase);
      editor.putString(LOCATION, location);
      editor.putString(TRANSLATION, translation);
      editor.putString(TO_WHOM, toWhom);
      editor.putString(NOTES, notes);
      editor.putString(AUDIO_FILE, audioFile);
      editor.commit();
   }

   public static void saveQAInfo(Context context, long date, String phrase,
         String answer, String location, String toWhom, String notes,
         String audioFile, int asked, int asnwered)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();
      editor.putLong(DATE, date);
      editor.putString(PHRASE, phrase);
      editor.putString(ANSWER, answer);
      editor.putString(LOCATION, location);
      editor.putString(TO_WHOM, toWhom);
      editor.putString(NOTES, notes);
      editor.putString(AUDIO_FILE, audioFile);
      editor.commit();
   }

   public static String getLanguage(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getString(LANGUAGE_FILTER,
            context.getString(R.string.all_languages));
   }

   public static String getSortColumn(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getString(SORT_COLUMN,
            DbContract.Words.COLUMN_NAME_WORD);
   }

   public static int getSortColumnId(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getInt(SORT_COLUMN_ID, SORT_COLUMN_PHRASE);
   }

   public static boolean getIsAscending(Context context)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      return sharedPrefs.getBoolean(SORT_ASCENDING, true);
   }

   public static void saveAll(Context context, int id, String language,
         int column, boolean ascending)
   {
      SharedPreferences sharedPrefs = context.getSharedPreferences(
            SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPrefs.edit();

      editor.putInt(CURRENT_KID_ID, id);
      editor.putString(LANGUAGE_FILTER, language);
      editor.putInt(SORT_COLUMN_ID, column);
      editor.putBoolean(SORT_ASCENDING, ascending);

      editor.commit();
   }
}
