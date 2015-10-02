package com.nadajp.littletalkers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.database.DbContract.Questions;
import com.nadajp.littletalkers.database.DbContract.Words;
import com.nadajp.littletalkers.server.littletalkersapi.model.Kid;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserDataWrapper;
import com.nadajp.littletalkers.server.littletalkersapi.model.Word;

import java.util.ArrayList;
import java.util.List;

public class DbSingleton
{
   private static DbSingleton sInstance;
   private Context mContext;
   private SQLiteDatabase mDb;

   private DbSingleton(Context appContext)
   {
      // Create my one and only handle the database.
      DatabaseHelper dbHelper = new DatabaseHelper(appContext);
      mDb = dbHelper.getWritableDatabase();
      mContext = appContext;
   }

   public static void init(Context appContext)
   {
      sInstance = new DbSingleton(appContext);
   }

   public static DbSingleton get()
   {
      if (sInstance == null)
      {
         throw new java.lang.IllegalStateException(
               "DbSingleton has not been initialized.");
      }
      return sInstance;
   }

   public int getNumberOfKids()
   {
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery("SELECT COUNT(*) FROM Kids", null);
         cursor.moveToFirst();
         return cursor.getInt(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public boolean isEmpty()
   {
      if (getNumberOfKids() == 0)
      {
         return true;
      }
      return false;
   }

   public int getNumberOfWords(int kidId)
   {
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery("SELECT COUNT(*) FROM Words WHERE "
               + Words.COLUMN_NAME_KID + " = " + kidId, null);
         cursor.moveToFirst();
         return cursor.getInt(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public int getNumberOfQAs(int kidId)
   {
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery("SELECT COUNT(*) FROM Questions WHERE "
               + Questions.COLUMN_NAME_KID + " = " + kidId, null);
         cursor.moveToFirst();
         return cursor.getInt(0);
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
   }

   public String getKidName(int kidId)
   {
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery("SELECT " + Kids.COLUMN_NAME_NAME
               + " FROM Kids WHERE " + Kids._ID + " = " + kidId,
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

   public Cursor getKidsForSpinner()
   {
      String query = "SELECT _id, name, "
            + Kids.COLUMN_NAME_PICTURE_URI + " FROM Kids";
      return mDb.rawQuery(query, null);
   }

   public Cursor getKidsForList()
   {
      String query = "SELECT _id, name, "
            + Kids.COLUMN_NAME_PICTURE_URI + ", "
            + Kids.COLUMN_NAME_BIRTHDATE_MILLIS + " FROM Kids";
      return mDb.rawQuery(query, null);
   }

   public Cursor getWords(int kidId, String sortColumn, boolean bAscending,
         String language)
   {
      String query;

      if (language.equals(mContext.getString(R.string.all_languages)))
      {
         query = "SELECT _id, " + Words.COLUMN_NAME_WORD + ", "
               + Words.COLUMN_NAME_DATE + ", "
               + Words.COLUMN_NAME_AUDIO_FILE + " FROM Words WHERE "
               + Words.COLUMN_NAME_KID + " = " + kidId
               + " ORDER BY " + sortColumn;
      } else
      {
         query = "SELECT _id, " + Words.COLUMN_NAME_WORD + ", "
               + Words.COLUMN_NAME_DATE + ", "
               + Words.COLUMN_NAME_AUDIO_FILE + " FROM Words WHERE "
               + Words.COLUMN_NAME_KID + " = " + kidId + " AND "
               + Words.COLUMN_NAME_LANGUAGE + " = '" + language
               + "' ORDER BY " + sortColumn;
      }
      if (bAscending)
      {
         query += " ASC";
      } else
      {
         query += " DESC";
      }

      return mDb.rawQuery(query, null);
   }

   public Cursor getQuestions(int kidId, String sortColumn, boolean bAscending,
         String language)
   {
      String query;

      if (language.equals(mContext.getString(R.string.all_languages)))
      {
         query = "SELECT _id, " + Questions.COLUMN_NAME_QUESTION
               + ", " + Questions.COLUMN_NAME_ANSWER + ", "
               + Questions.COLUMN_NAME_ASKED + ","
               + Questions.COLUMN_NAME_ANSWERED + ","
               + Questions.COLUMN_NAME_TOWHOM + ","
               + Questions.COLUMN_NAME_DATE + ", "
               + Questions.COLUMN_NAME_AUDIO_FILE
               + " FROM Questions WHERE "
               + Questions.COLUMN_NAME_KID + " = " + kidId
               + " ORDER BY " + sortColumn;
      } else
      {
         query = "SELECT _id, " + Questions.COLUMN_NAME_QUESTION
               + ", " + Questions.COLUMN_NAME_ANSWER + ", "
               + Questions.COLUMN_NAME_ASKED + ","
               + Questions.COLUMN_NAME_ANSWERED + ","
               + Questions.COLUMN_NAME_TOWHOM + ","
               + Questions.COLUMN_NAME_DATE + ", "
               + Questions.COLUMN_NAME_AUDIO_FILE
               + " FROM Questions WHERE "
               + Questions.COLUMN_NAME_KID + " = " + kidId + " AND "
               + Questions.COLUMN_NAME_LANGUAGE + " = '" + language
               + "' ORDER BY " + sortColumn;
      }
      if (bAscending)
      {
         query += " ASC";
      } else
      {
         query += " DESC";
      }

      return mDb.rawQuery(query, null);
   }

   public Cursor getWordsForExport(int kidId)
   {
      String query;

      query = "SELECT * FROM Words WHERE " + Words.COLUMN_NAME_KID
            + " = " + kidId + " ORDER BY " + Words.COLUMN_NAME_DATE + " ASC";

      return mDb.rawQuery(query, null);
   }

   public Cursor getQAForExport(long kidId)
   {
      String query;

      query = "SELECT * FROM Questions WHERE "
            + Questions.COLUMN_NAME_KID + " = " + kidId
            + " ORDER BY " + Questions.COLUMN_NAME_DATE + " ASC";

      return mDb.rawQuery(query, null);
   }

   public String getTranslation(long wordId)
   {
      String query = "SELECT " + Words.COLUMN_NAME_TRANSLATION
            + " FROM Words WHERE _id = " + wordId;

      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
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

   public Cursor getWordHistory(int kidId, long wordId)
   {
      String query;
      String translation = getTranslation(wordId);

      query = "SELECT _id, " + Words.COLUMN_NAME_WORD + ", "
            + Words.COLUMN_NAME_DATE + ", "
            + Words.COLUMN_NAME_AUDIO_FILE + " FROM Words WHERE "
            + Words.COLUMN_NAME_KID + " = " + kidId + " AND "
            + Words.COLUMN_NAME_TRANSLATION + " = '" + translation
            + "' ORDER BY " + Words.COLUMN_NAME_DATE + " ASC";

      return mDb.rawQuery(query, null);
   }

   public List<String> getLanguages(int kidId)
   {
      String query = "SELECT DISTINCT " + Words.COLUMN_NAME_LANGUAGE
            + " FROM Words WHERE " + Words.COLUMN_NAME_KID + " = "
            + kidId + " ORDER BY " + Words.COLUMN_NAME_LANGUAGE
            + " ASC";

      List<String> languages = new ArrayList<String>();
      Cursor cursor = mDb.rawQuery(query, null);

      // looping through all rows and adding to list
      if (cursor.moveToFirst())
      {
         do
         {
            languages.add(cursor.getString(cursor
                  .getColumnIndex(Words.COLUMN_NAME_LANGUAGE)));
         } while (cursor.moveToNext());
      }
      cursor.close();
      return languages;
   }

   public Cursor getWordDetails(long wordId)
   {
      String query = "SELECT * FROM Words WHERE _id = " + wordId;
      return mDb.rawQuery(query, null);
   }

   public Cursor getQuestionDetails(long questionID)
   {
      String query = "SELECT * FROM Questions WHERE _id = " + questionID;
      return mDb.rawQuery(query, null);
   }

   public Cursor getKidDetails(int kidId)
   {
      String query = "SELECT * FROM Kids WHERE _id = " + kidId;
      return mDb.rawQuery(query, null);
   }

   public String getDefaultLanguage(String kidName)
   {
      String query = "SELECT " + Kids.COLUMN_NAME_DEFAULT_LANGUAGE
            + " FROM Kids WHERE name = ?";
      // Log.i(DEBUG_TAG, query);
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, new String[] { kidName });
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

   public String getDefaultLanguage(int kidId)
   {
      String query = "SELECT " + Kids.COLUMN_NAME_DEFAULT_LANGUAGE
            + " FROM Kids WHERE _id = ?";
      Cursor cursor = null;
      String arg = Long.valueOf(kidId).toString();
      try
      {
         cursor = mDb.rawQuery(query, new String[] { arg });
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

   public String[] getDefaults(int kidId)
   {
      String[] rtn = new String[2];
      String query = "SELECT " + Kids.COLUMN_NAME_DEFAULT_LANGUAGE
            + "," + Kids.COLUMN_NAME_DEFAULT_LOCATION
            + " FROM Kids WHERE _id = " + kidId;
      // Log.i(DEBUG_TAG, query);
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
         cursor.moveToFirst();
         rtn[0] = cursor.getString(cursor
               .getColumnIndex(Kids.COLUMN_NAME_DEFAULT_LANGUAGE));
         rtn[1] = cursor.getString(cursor
               .getColumnIndex(Kids.COLUMN_NAME_DEFAULT_LOCATION));
      } finally
      {
         if (cursor != null)
         {
            cursor.close();
         }
      }
      return rtn;
   }

   public int getLastAddedKid()
   {
      String query = "SELECT _id FROM Kids ORDER BY _id DESC LIMIT 1";
      // Log.i(DEBUG_TAG, query);
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
         if (cursor != null && cursor.getCount() > 0)
         {
            cursor.moveToFirst();
            return cursor.getInt(0);
         }
         return -1;
      } finally
      {
         if (cursor != null)
            cursor.close();
      }

   }

   public int saveKid(String name, String location, String language,
         String pictureUri, long birthdayMillis)
   {
      // check if name already exists
      String query = "SELECT * FROM Kids WHERE name = ?";
      Cursor cursor = mDb.rawQuery(query, new String[] { name });
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return -1;
      }
      cursor.close();

      ContentValues values = new ContentValues();
      values.put(Kids.COLUMN_NAME_NAME, name);
      values.put(Kids.COLUMN_NAME_DEFAULT_LOCATION, location);
      values.put(Kids.COLUMN_NAME_DEFAULT_LANGUAGE, language);
      values.put(Kids.COLUMN_NAME_PICTURE_URI, pictureUri);
      values.put(Kids.COLUMN_NAME_BIRTHDATE_MILLIS, birthdayMillis);

      // Insert row and return row id
      return (int) mDb.insert("Kids", null, values);
   }

   public boolean updateKid(int id, String name, String location,
         String language, String pictureUri, long birthdayMillis)
   {
      // check if another kid with this name already exists
      String query = "SELECT " + Kids.COLUMN_NAME_NAME
            + " FROM Kids WHERE " + Kids.COLUMN_NAME_NAME
            + " = ? AND _id != " + id;

      // if there is a different kid (different ID) with the same name, return
      // false
      Cursor cursor = mDb.rawQuery(query, new String[] { name });
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return false;
      }

      cursor.close();

      // otherwise, update all values
      ContentValues values = new ContentValues();
      values.put(Kids.COLUMN_NAME_NAME, name);
      values.put(Kids.COLUMN_NAME_DEFAULT_LANGUAGE, language);
      values.put(Kids.COLUMN_NAME_DEFAULT_LOCATION, location);
      values.put(Kids.COLUMN_NAME_PICTURE_URI, pictureUri);
      values.put(Kids.COLUMN_NAME_BIRTHDATE_MILLIS, birthdayMillis);

      // update
      mDb.update("Kids", values, "_id=" + id, null);
      return true;
   }

   public void deleteKids(long[] ids)
   {
      for (long id : ids)
      {
         mDb.delete("Kids", "_id = " + id, null);
         mDb.delete("Words", Words.COLUMN_NAME_KID + " = " + id,
               null);
         mDb.delete("Questions", Questions.COLUMN_NAME_KID + " = "
               + id, null);
      }
   }

   public String getPicturePath(int id)
   {
      String query = "SELECT " + Kids.COLUMN_NAME_PICTURE_URI
            + " FROM Kids WHERE _id = " + id;
      Cursor cursor = null;
      try
      {
         cursor = mDb.rawQuery(query, null);
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

   public void deleteWords(long[] ids)
   {
      for (long id : ids)
      {
         mDb.delete("Words", "_id = " + id, null);
      }
   }

   public void deleteQuestions(long[] ids)
   {
      for (long id : ids)
      {
         mDb.delete("Questions", "_id = " + id, null);
      }
   }

   public long saveWord(Word word)
   {
      // check if word already exists for this kid
      String query = "SELECT word FROM Words WHERE "
            + Words.COLUMN_NAME_KID + " = " + word.getKidId()
            + " AND word = ?";
      Cursor cursor = mDb.rawQuery(query, new String[] { word.getWord() });
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return -1;
      }
      cursor.close();
      ContentValues values = new ContentValues();
      values.put(Words.COLUMN_NAME_KID, word.getKidId());
      values.put(Words.COLUMN_NAME_WORD, word.getWord());
      values.put(Words.COLUMN_NAME_LANGUAGE, word.getLanguage());
      values.put(Words.COLUMN_NAME_DATE, word.getDate());
      values.put(Words.COLUMN_NAME_LOCATION, word.getLocation());
      values.put(Words.COLUMN_NAME_AUDIO_FILE,
            word.getAudioFileUri());
      values.put(Words.COLUMN_NAME_TRANSLATION,
            word.getTranslation());
      values.put(Words.COLUMN_NAME_TOWHOM, word.getToWhom());
      values.put(Words.COLUMN_NAME_NOTES, word.getNotes());

      // Inserting Row
      return mDb.insert("Words", null, values);
   }

   public long saveWord(int kidId, String word, String language, long date,
         String location, String audioFile, String translation, String towhom,
         String notes)
   {
      // check if word already exists for this kid
      String query = "SELECT word FROM Words WHERE "
            + Words.COLUMN_NAME_KID + " = " + kidId
            + " AND word = ?";
      Cursor cursor = mDb.rawQuery(query, new String[] { word });
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return -1;
      }
      cursor.close();
      ContentValues values = new ContentValues();
      values.put(Words.COLUMN_NAME_KID, kidId);
      values.put(Words.COLUMN_NAME_WORD, word);
      values.put(Words.COLUMN_NAME_LANGUAGE, language);
      values.put(Words.COLUMN_NAME_DATE, date);
      values.put(Words.COLUMN_NAME_LOCATION, location);
      values.put(Words.COLUMN_NAME_AUDIO_FILE, audioFile);
      values.put(Words.COLUMN_NAME_TRANSLATION, translation);
      values.put(Words.COLUMN_NAME_TOWHOM, towhom);
      values.put(Words.COLUMN_NAME_NOTES, notes);

      // Inserting Row
      return mDb.insert("Words", null, values);
   }

   public long saveQuestion(int kidId, String question, String answer,
         int asked, int answered, String towhom, String language, long date,
         String location, String audioFile, String notes)
   {
      // check if qa already exists for this kid
      String query = "SELECT question FROM Questions WHERE "
            + Questions.COLUMN_NAME_KID + " = " + kidId
            + " AND question = ? AND answer = ?";

      Cursor cursor = mDb.rawQuery(query, new String[] { question, answer });
      if (cursor.getCount() > 0)
      {
         cursor.close();
         return -1;
      }
      cursor.close();
      ContentValues values = new ContentValues();
      values.put(Questions.COLUMN_NAME_KID, kidId);
      values.put(Questions.COLUMN_NAME_QUESTION, question);
      values.put(Questions.COLUMN_NAME_ANSWER, answer);
      values.put(Questions.COLUMN_NAME_TOWHOM, towhom);
      values.put(Questions.COLUMN_NAME_ASKED, asked);
      values.put(Questions.COLUMN_NAME_ANSWERED, answered);
      values.put(Questions.COLUMN_NAME_LANGUAGE, language);
      values.put(Questions.COLUMN_NAME_DATE, date);
      values.put(Questions.COLUMN_NAME_LOCATION, location);
      values.put(Questions.COLUMN_NAME_AUDIO_FILE, audioFile);
      values.put(Questions.COLUMN_NAME_NOTES, notes);

      // Inserting Row
      return mDb.insert("Questions", null, values);
   }

   public boolean updateWord(long wordId, int kidId, String word,
         String language, long date, String location, String audioFile,
         String translation, String towhom, String notes)
   {
      ContentValues values = new ContentValues();
      values.put(Words.COLUMN_NAME_KID, kidId);
      values.put(Words.COLUMN_NAME_WORD, word);
      values.put(Words.COLUMN_NAME_LANGUAGE, language);
      values.put(Words.COLUMN_NAME_DATE, date);
      values.put(Words.COLUMN_NAME_LOCATION, location);
      values.put(Words.COLUMN_NAME_AUDIO_FILE, audioFile);
      values.put(Words.COLUMN_NAME_TRANSLATION, translation);
      values.put(Words.COLUMN_NAME_TOWHOM, towhom);
      values.put(Words.COLUMN_NAME_NOTES, notes);

      // update
      mDb.update("Words", values, "_id=" + wordId, null);
      return true;
   }

   public boolean updateQuestion(long questionID, int kidId, String question,
         String answer, int asked, int answered, String towhom,
         String language, long date, String location, String audioFile,
         String notes)
   {
      ContentValues values = new ContentValues();
      values.put(Questions.COLUMN_NAME_KID, kidId);
      values.put(Questions.COLUMN_NAME_QUESTION, question);
      values.put(Questions.COLUMN_NAME_ANSWER, answer);
      values.put(Questions.COLUMN_NAME_TOWHOM, towhom);
      values.put(Questions.COLUMN_NAME_ASKED, asked);
      values.put(Questions.COLUMN_NAME_ANSWERED, answered);
      values.put(Questions.COLUMN_NAME_LANGUAGE, language);
      values.put(Questions.COLUMN_NAME_DATE, date);
      values.put(Questions.COLUMN_NAME_LOCATION, location);
      values.put(Questions.COLUMN_NAME_AUDIO_FILE, audioFile);
      values.put(Questions.COLUMN_NAME_NOTES, notes);

      // Update Row
      mDb.update("Questions", values, "_id=" + questionID, null);
      return true;

   }

   public void updateAudoFile(long wordId, String audioFile)
   {
      ContentValues values = new ContentValues();
      values.put(Words.COLUMN_NAME_AUDIO_FILE, audioFile);
      mDb.update("Words", values, "_id=" + wordId, null);
   }

   public Cursor getKidsForExport()
   {
      String query = "SELECT * FROM Kids";
      return mDb.rawQuery(query, null);
   }

   public Cursor getAllWordsForBackup()
   {
      String query = "SELECT * FROM Words";
      return mDb.rawQuery(query, null);
   }

   public Cursor getKidsForSync()
   {
      String query = "SELECT * FROM Kids WHERE is_dirty = ?";
      return mDb.rawQuery(query, new String[] { "1" });
   }

   public Cursor getWordsForSync()
   {
      String query = "SELECT * FROM Words WHERE is_dirty = ?";
      return mDb.rawQuery(query, new String[] { "1" });
   }

   public void setNotDirty(UserDataWrapper data)
   {
      List<Kid> kids = data.getKids();
      List<Word> words = data.getWords();

      ContentValues values = new ContentValues();
      values.put("is_dirty", 0);

      for (Kid kid : kids)
      {
         mDb.update("Kids", values, "_id=?",
               new String[] { (kid.getId()).toString() });
      }
      for (Word word : words)
      {
         mDb.update("Words", values, Words.COLUMN_NAME_WORD + "=?",
               new String[] { (word.getWord()) });
      }
   }
}
