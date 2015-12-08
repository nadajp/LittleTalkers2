package com.nadajp.littletalkers.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by nadajp on 6/30/15.
 */
public class TestDb extends AndroidTestCase {

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(DatabaseHelper.DB_NAME);
    }

    /*
       This function gets called before each test is executed to delete the database.  This makes
       sure that we always have a clean test.
    */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(DbContract.Kids.TABLE_NAME);
        tableNameHashSet.add(DbContract.Words.TABLE_NAME);
        tableNameHashSet.add(DbContract.Questions.TABLE_NAME);

        mContext.deleteDatabase(DatabaseHelper.DB_NAME);
        SQLiteDatabase db = new DatabaseHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain all three tables
        assertTrue("Error: Your database was created without all three tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + DbContract.Kids.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> KidsColumnHashSet = new HashSet<String>();
        KidsColumnHashSet.add(DbContract.Kids._ID);
        KidsColumnHashSet.add(DbContract.Kids.COLUMN_NAME_NAME);
        KidsColumnHashSet.add(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS);
        KidsColumnHashSet.add(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE);
        KidsColumnHashSet.add(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION);
        KidsColumnHashSet.add(DbContract.Kids.COLUMN_NAME_IS_DIRTY);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            KidsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required kids
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required kids entry columns",
                KidsColumnHashSet.isEmpty());
        db.close();
    }

    /*
       Students:  Here is where you will build code to test that we can insert and query the
       location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
       where you can uncomment out the "createNorthPoleLocationValues" function.  You can
       also make use of the ValidateCurrentRecord function from within TestUtilities.
   */
    public void testKidsTable() {
        insertKid();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */

    public void testWordsTable() {
        // First insert the kid, and then use the kidId to insert
        // the word. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        int kidId = (int) insertKid();

        // Make sure we have a valid row ID.
        assertFalse("Error: Kid Not Inserted Correctly", kidId == -1);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Weather): Create weather values
        ContentValues wordValues = TestUtils.createWordValues(kidId);

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long wordRowId = db.insert(DbContract.Words.TABLE_NAME, null, wordValues);
        assertTrue(wordRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor wordCursor = db.query(
                DbContract.Words.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from word query", wordCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtils.validateCurrentRecord("testInsertReadDb words failed to validate",
                wordCursor, wordValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from word query",
                wordCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        wordCursor.close();
        dbHelper.close();
    }

    public long insertKid() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = TestUtils.createKidValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long kidId = db.insert(DbContract.Kids.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(kidId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                DbContract.Kids.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from kids query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtils.validateCurrentRecord("Error: Kid Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from kid query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return kidId;
    }

    public static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(columnName,idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}