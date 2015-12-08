package com.nadajp.littletalkers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by nadajp on 6/30/15.
 */
public class TestUtils extends AndroidTestCase {

    static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    static final long BIRTHDATE = 1317268800000L;

    public static ContentValues createWordValues(int kidId){
        ContentValues wordValues = new ContentValues();
        wordValues.put(DbContract.Words.COLUMN_NAME_KID, kidId);
        wordValues.put(DbContract.Words.COLUMN_NAME_WORD, "Balka");
        wordValues.put(DbContract.Words.COLUMN_NAME_DATE, TEST_DATE);
        wordValues.put(DbContract.Words.COLUMN_NAME_LANGUAGE, "Croatian");
        wordValues.put(DbContract.Words.COLUMN_NAME_TRANSLATION, "Barka");
        wordValues.put(DbContract.Words.COLUMN_NAME_TOWHOM, "mama");
        wordValues.put(DbContract.Words.COLUMN_NAME_LOCATION, "West Chester");
        wordValues.put(DbContract.Words.COLUMN_NAME_AUDIO_FILE, "//littletalkers/audio/barka.3gp");
        return wordValues;
    }

    static ContentValues createSecondWordValues(int kidId){
        ContentValues wordValues = new ContentValues();
        wordValues.put(DbContract.Words.COLUMN_NAME_KID, kidId);
        wordValues.put(DbContract.Words.COLUMN_NAME_WORD, "Apple");
        wordValues.put(DbContract.Words.COLUMN_NAME_DATE, TEST_DATE);
        wordValues.put(DbContract.Words.COLUMN_NAME_LANGUAGE, "English");
        wordValues.put(DbContract.Words.COLUMN_NAME_TOWHOM, "Mama");
        wordValues.put(DbContract.Words.COLUMN_NAME_LOCATION, "West Chester");
        wordValues.put(DbContract.Words.COLUMN_NAME_AUDIO_FILE, "//littletalkers/audio/apple.3gp");
        return wordValues;
    }

    public static ContentValues createKidValues(){
        ContentValues kidValues = new ContentValues();
        kidValues.put(DbContract.Kids.COLUMN_NAME_NAME, "Leo");
        kidValues.put(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS, BIRTHDATE);
        kidValues.put(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE, "Croatian");
        kidValues.put(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION, "West Chester PA");
        kidValues.put(DbContract.Kids.COLUMN_NAME_PICTURE_URI, "//pictures/Leo.jpg");
        return kidValues;
    }

    public static ContentValues createSecondKidValues(){
        ContentValues kidValues = new ContentValues();
        kidValues.put(DbContract.Kids.COLUMN_NAME_NAME, "Hazel");
        kidValues.put(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS, BIRTHDATE);
        kidValues.put(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE, "German");
        kidValues.put(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION, "Luxembourg");
        kidValues.put(DbContract.Kids.COLUMN_NAME_PICTURE_URI, "//pictures/Hazel.jpg");
        return kidValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
