package com.nadajp.littletalkers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.database.DbContract.Words;
import com.nadajp.littletalkers.database.DbContract.Questions;

/**
 * This creates/opens the database.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
   private static final String DEBUG_TAG = "DatabaseHelper";
   private static final int DB_VERSION = 2;
   static final String DB_NAME = "littletalkers_db";
   private Context ctxt = null;

   public DatabaseHelper(Context ctxt)
   {
      super(ctxt, DB_NAME, null, DB_VERSION);
      this.ctxt = ctxt;
   }

   // Table Create Statements

   // Kids table create statement
   private static final String CREATE_TABLE_KIDS = "CREATE TABLE "
         + Kids.TABLE_NAME + "(" 
         + Kids._ID + " INTEGER PRIMARY KEY," 
         + Kids.COLUMN_NAME_NAME + " TEXT UNIQUE," 
         + Kids.COLUMN_NAME_BIRTHDATE_MILLIS + " INTEGER,"
         + Kids.COLUMN_NAME_DEFAULT_LOCATION + " TEXT,"
         + Kids.COLUMN_NAME_DEFAULT_LANGUAGE + " TEXT,"
         + Kids.COLUMN_NAME_PICTURE_URI + " TEXT,"
         + Kids.COLUMN_NAME_IS_DIRTY + " INTEGER DEFAULT 1);";

   // Words table create statement
   private static final String CREATE_TABLE_WORDS = "CREATE TABLE "
         + Words.TABLE_NAME + "(" 
         + Words._ID + " INTEGER PRIMARY KEY," 
         + Words.COLUMN_NAME_KID + " INTEGER,"
         + Words.COLUMN_NAME_WORD + " TEXT," 
         + Words.COLUMN_NAME_AUDIO_FILE + " TEXT,"
         + Words.COLUMN_NAME_LANGUAGE + " TEXT,"
         + Words.COLUMN_NAME_DATE + " INTEGER,"
         + Words.COLUMN_NAME_LOCATION + " TEXT,"
         + Words.COLUMN_NAME_TRANSLATION + " TEXT,"
         + Words.COLUMN_NAME_TOWHOM + " TEXT,"
         + Words.COLUMN_NAME_NOTES + " TEXT,"
         + Words.COLUMN_NAME_IS_DIRTY + " INTEGER DEFAULT 1);";

   // Questions table create statement
   private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
         + Questions.TABLE_NAME + "(" 
         + Questions._ID + " INTEGER PRIMARY KEY," 
         + Questions.COLUMN_NAME_KID + " INTEGER," 
         + Questions.COLUMN_NAME_QUESTION + " TEXT,"
         + Questions.COLUMN_NAME_ANSWER + " TEXT,"
         + Questions.COLUMN_NAME_TOWHOM + " TEXT,"
         + Questions.COLUMN_NAME_ASKED + " INTEGER,"
         + Questions.COLUMN_NAME_ANSWERED + " INTEGER,"
         + Questions.COLUMN_NAME_LANGUAGE + " TEXT,"
         + Questions.COLUMN_NAME_DATE + " INTEGER,"
         + Questions.COLUMN_NAME_LOCATION + " TEXT,"
         + Questions.COLUMN_NAME_AUDIO_FILE + " TEXT,"
         + Questions.COLUMN_NAME_NOTES + " TEXT,"
         + Questions.COLUMN_NAME_IS_DIRTY + " INTEGER DEFAULT 1);";

   @Override
   public void onCreate(SQLiteDatabase db)
   {
      // creating required tables
      db.execSQL(CREATE_TABLE_KIDS);
      db.execSQL(CREATE_TABLE_WORDS);
      db.execSQL(CREATE_TABLE_QUESTIONS);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      // Logs that the database is being upgraded
      Log.w(DEBUG_TAG, "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");

      switch (oldVersion) 
      {
        case 1:
           db.execSQL("ALTER TABLE " + 
                 Kids.TABLE_NAME + " ADD COLUMN " + "is_dirty" + " INTEGER DEFAULT 1");
           db.execSQL("ALTER TABLE " + 
                 Words.TABLE_NAME + " ADD COLUMN " + "is_dirty" + " INTEGER DEFAULT 1");
           db.execSQL("ALTER TABLE " + 
                 Questions.TABLE_NAME + " ADD COLUMN " + "is_dirty" + " INTEGER DEFAULT 1");
          // we want both updates, so no break statement here...
        //case 2:
          //db.execSQL(DATABASE_CREATE_someothertable); 
      }
   }

   @Override
   public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      onUpgrade(db, oldVersion, newVersion);
   }
}
