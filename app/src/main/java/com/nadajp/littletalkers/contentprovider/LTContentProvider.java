package com.nadajp.littletalkers.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.nadajp.littletalkers.database.DatabaseHelper;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.database.DbContract.Questions;
import com.nadajp.littletalkers.database.DbContract.Words;

public class LTContentProvider extends ContentProvider
{
    private static final String DEBUG_TAG = "LTContentProvider";

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
   /*
    * Defines a handle to the database helper object. The MainDatabaseHelper
    * class is defined in a following snippet.
    */
   private DatabaseHelper mDbHelper;

   /**
    * Content authority for this provider.
    */
   public static final String CONTENT_AUTHORITY = DbContract.AUTHORITY;

   /**
    * Base URI. (content://com.example.android.network.sync.basicsyncadapter)
    */
   public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
         + CONTENT_AUTHORITY);

   // The constants below represent individual URI routes, as IDs. Every URI
   // pattern recognized by
   // this ContentProvider is defined using sUriMatcher.addURI(), and associated
   // with one of these
   // IDs.
   //
   // When a incoming URI is run through sUriMatcher, it will be tested against
   // the defined
   // URI patterns, and the corresponding route ID will be returned.
   /**
    * URI ID for route: /kids
    */
   public static final int KIDS = 1;
   /**
    * URI ID for route: /kids/{ID}
    */
   public static final int KID_ID = 2;
   /**
    * URI ID for route: /words
    */
   public static final int WORDS = 10;
   /**
    * URI ID for route: /words/{LANGUAGE}
    */
   public static final int WORDS_WITH_LANGUAGE = 11;
    /**
     * URI ID for route: /words/{ID}
     */
   public static final int WORD_ID = 12;
   /**
    * URI ID for route: /questions
    */
   public static final int QUESTIONS = 20;
    /**
     * URI ID for route: /questions
     */
    public static final int QUESTIONS_WITH_LANGUAGE = 21;
   /**
    * URI ID for route: /questions/{ID}
    */
   public static final int QUESTION_ID = 22;

   /**
    * UriMatcher, used to decode incoming URIs.
    */
   static UriMatcher buildUriMatcher() {
       // I know what you're thinking.  Why create a UriMatcher when you can use regular
       // expressions instead?  Because you're not crazy, that's why.

       // All paths added to the UriMatcher have a corresponding code to return when a match is
       // found.  The code passed into the constructor represents the code to return for the root
       // URI.  It's common to use NO_MATCH as the code for this case.
       final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

       matcher.addURI(CONTENT_AUTHORITY, Kids.TABLE_NAME, KIDS);
       matcher.addURI(CONTENT_AUTHORITY, Kids.TABLE_NAME + "/#", KID_ID);
       matcher.addURI(CONTENT_AUTHORITY, Words.TABLE_NAME, WORDS);
       matcher.addURI(CONTENT_AUTHORITY, Words.TABLE_NAME + "LANGUAGE/*", WORDS_WITH_LANGUAGE);
       matcher.addURI(CONTENT_AUTHORITY, Words.TABLE_NAME + "/#", WORD_ID);
       matcher.addURI(CONTENT_AUTHORITY, Questions.TABLE_NAME, QUESTIONS);
       matcher.addURI(CONTENT_AUTHORITY, Questions.TABLE_NAME + "LANGUAGE/*", QUESTIONS_WITH_LANGUAGE);
       matcher.addURI(CONTENT_AUTHORITY, Questions.TABLE_NAME + "/#", QUESTION_ID);

       return matcher;
   }

   public boolean onCreate()
   {
      /*
       * Creates a new helper object. This method always returns quickly. Notice
       * that the database itself isn't created or opened until
       * SQLiteOpenHelper.getWritableDatabase is called
       */
      mDbHelper = new DatabaseHelper(getContext());
      return true;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection,
         String[] selectionArgs, String sortOrder)
   {
      // Using SQLiteQueryBuilder instead of query() method
      SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

      switch (sUriMatcher.match(uri))
      {
      case KIDS:
         queryBuilder.setTables(Kids.TABLE_NAME);
         break;
      case KID_ID:
         // adding the ID to the original query
         queryBuilder.setTables(Kids.TABLE_NAME);
         queryBuilder.appendWhere(Kids._ID + "=" + uri.getLastPathSegment());
         break;
      case WORDS:
         queryBuilder.setTables(Words.TABLE_NAME);
         break;
      case WORDS_WITH_LANGUAGE:
         queryBuilder.setTables(Words.TABLE_NAME);
         queryBuilder.appendWhere(Words.COLUMN_NAME_LANGUAGE + " = " + uri.getLastPathSegment());
         break;
      case WORD_ID:
         queryBuilder.setTables(Words.TABLE_NAME);
         queryBuilder.appendWhere(Words._ID + "=" + uri.getLastPathSegment());
         break;
      case QUESTIONS:
         queryBuilder.setTables(Questions.TABLE_NAME);
         break;
      case QUESTIONS_WITH_LANGUAGE:
          queryBuilder.setTables(Questions.TABLE_NAME);
          queryBuilder.appendWhere(Questions.COLUMN_NAME_LANGUAGE + " = " + uri.getLastPathSegment());
          break;
      case QUESTION_ID:
         queryBuilder.setTables(Questions.TABLE_NAME);
         queryBuilder.appendWhere(Questions._ID + "="
               + uri.getLastPathSegment());
         break;
      default:
         throw new IllegalArgumentException("Unknown URI: " + uri);
      }

       Cursor cursor = queryBuilder.query(mDbHelper.getReadableDatabase(),
            projection,
            selection,
            selectionArgs,
            null,          // group by
            null,          // having
            sortOrder);
      // make sure that potential listeners are getting notified
      cursor.setNotificationUri(getContext().getContentResolver(), uri);

      return cursor;
   }

   @Override
   public int delete(Uri uri, String selection, String[] selectionArgs)
   {
      final SQLiteDatabase db = mDbHelper.getWritableDatabase();
      final int match = sUriMatcher.match(uri);
      int rowsDeleted;
      // this makes delete all rows return the number of rows deleted
      if (null == selection)
         selection = "1";
      switch (match)
      {
      case KIDS:
         rowsDeleted = db.delete(Kids.TABLE_NAME, selection, selectionArgs);
         break;
      case KID_ID:
          rowsDeleted = db.delete(Kids.TABLE_NAME, selection, selectionArgs);
          break;
      case WORDS:
         rowsDeleted = db.delete(Words.TABLE_NAME, selection, selectionArgs);
         break;
      case WORD_ID:
          rowsDeleted = db.delete(Words.TABLE_NAME, selection, selectionArgs);
          break;
      case QUESTIONS:
         rowsDeleted = db.delete(Questions.TABLE_NAME, selection, selectionArgs);
         break;
      case QUESTION_ID:
          rowsDeleted = db.delete(Questions.TABLE_NAME, selection, selectionArgs);
          break;
      default:
         throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
      // Because a null deletes all rows
      if (rowsDeleted != 0)
      {
         getContext().getContentResolver().notifyChange(uri, null);
      }
      return rowsDeleted;
   }

   @Override
   public String getType(Uri uri)
   {
      // Use the Uri Matcher to determine what kind of URI this is.
      final int match = sUriMatcher.match(uri);

      switch (match) {
          // Student: Uncomment and fill out these two cases
          case KIDS:
              return Kids.CONTENT_TYPE;
          case KID_ID:
              return Kids.CONTENT_ITEM_TYPE;
          case WORDS:
              return Words.CONTENT_TYPE;
          case WORDS_WITH_LANGUAGE:
              return Words.CONTENT_TYPE;
          case WORD_ID:
              return Words.CONTENT_ITEM_TYPE;
          case QUESTIONS:
              return Questions.CONTENT_TYPE;
          case QUESTIONS_WITH_LANGUAGE:
              return Questions.CONTENT_TYPE;
          case QUESTION_ID:
              return Questions.CONTENT_ITEM_TYPE;
          default:
              throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
   }

   @Override
   public int update(Uri uri, ContentValues values, String selection,
         String[] selectionArgs)
   {
      final SQLiteDatabase db = mDbHelper.getWritableDatabase();
      final int match = sUriMatcher.match(uri);
      int rowsUpdated;

      switch (match)
      {
      case KIDS:
         rowsUpdated = db.update(Kids.TABLE_NAME, values, selection, selectionArgs);
         break;
      case WORDS:
         rowsUpdated = db.update(Words.TABLE_NAME, values, selection, selectionArgs);
         break;
      case QUESTIONS:
         rowsUpdated = db.update(Questions.TABLE_NAME, values, selection, selectionArgs);
         break;
      default:
         throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
      if (rowsUpdated != 0)
      {
         getContext().getContentResolver().notifyChange(uri, null);
      }
      return rowsUpdated;
   }

   @Override
   public Uri insert(Uri uri, ContentValues values)
   {
      final SQLiteDatabase db = mDbHelper.getWritableDatabase();
      final int match = sUriMatcher.match(uri);
      Uri returnUri;

      switch (match)
      {
      case KIDS:
      {
         long _id = db.insert(Kids.TABLE_NAME, null, values);
         if (_id > 0)
            returnUri = Kids.buildKidsUri(_id);
         else
            throw new android.database.SQLException(
                  "Failed to insert row into " + uri);
         break;
      }
      case WORDS:
      {
         long _id = db.insert(Words.TABLE_NAME, null, values);
         if (_id > 0)
            returnUri = Words.buildWordUri(_id);
         else
            throw new android.database.SQLException(
                  "Failed to insert row into " + uri);
         break;
      }
      case QUESTIONS:
      {
         long _id = db.insert(Questions.TABLE_NAME, null, values);
         if (_id > 0)
            returnUri = Questions.buildQuestionsUri(_id);
         else
            throw new android.database.SQLException(
                  "Failed to insert row into " + uri);
         break;
      }
      default:
         throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
      getContext().getContentResolver().notifyChange(uri, null);
      return returnUri;
   }
   
   @Override
   public int bulkInsert(Uri uri, ContentValues[] values) {
       final SQLiteDatabase db = mDbHelper.getWritableDatabase();
       final int match = sUriMatcher.match(uri);
       int returnCount = 0;
       switch (match) {
           case WORDS:
               db.beginTransaction();
               try {
                   for (ContentValues value : values) {
                       long _id = db.insert(Words.TABLE_NAME, null, value);
                       if (_id != -1) {
                           returnCount++;
                       }
                   }
                   db.setTransactionSuccessful();
               } finally {
                   db.endTransaction();
               }
               break;
           case QUESTIONS:
               db.beginTransaction();
               try {
                   for (ContentValues value : values) {
                       long _id = db.insert(Questions.TABLE_NAME, null, value);
                       if (_id != -1) {
                           returnCount++;
                       }
                   }
                   db.setTransactionSuccessful();
               } finally {
                   db.endTransaction();
               }
               break;
           default:
               return super.bulkInsert(uri, values);
       }
       getContext().getContentResolver().notifyChange(uri, null);
       return returnCount;
   }


    public String getDefaultLanguage(int kidId)
    {
        Cursor cursor = null;
        String arg = Long.valueOf(kidId).toString();
        try
        {
            cursor = query(Kids.buildKidsUri(kidId),
                           new String[] { Kids.COLUMN_NAME_DEFAULT_LANGUAGE },
                           null,
                           null,
                           null);
            cursor.moveToFirst();
            return cursor.getString(0);
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }
}
