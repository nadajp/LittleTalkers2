package com.nadajp.littletalkers.contentprovider;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.TestDb;
import com.nadajp.littletalkers.database.TestUtils;

/**
 * Created by nadajp on 7/6/15.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                DbContract.Kids.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                DbContract.Words.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                DbContract.Questions.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                DbContract.Kids.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Kids table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                DbContract.Words.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Words table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                DbContract.Questions.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Questions table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testInsertData(){
        insertKid();
        insertWord();
    }

    public void insertKid(){
        ContentValues kidValues = TestUtils.createKidValues();

        Uri kidUri = mContext.getContentResolver().insert(DbContract.Kids.CONTENT_URI, kidValues);
        long kidRowId = ContentUris.parseId(kidUri);
        assertTrue(kidRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                DbContract.Kids.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, kidValues);

        cursor = mContext.getContentResolver().query(
                DbContract.Kids.buildKidsUri(kidRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, kidValues);

    }

    public void insertWord(){
        ContentValues wordValues = TestUtils.createWordValues(1);

        Uri wordUri = mContext.getContentResolver().insert(DbContract.Words.CONTENT_URI, wordValues);
        long wordRowId = ContentUris.parseId(wordUri);
        assertTrue(wordRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                DbContract.Words.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, wordValues);

        cursor = mContext.getContentResolver().query(
                DbContract.Words.buildWordUri(wordRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, wordValues);

    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // ContentProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                LTContentProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: LTContentProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + LTContentProvider.CONTENT_AUTHORITY,
                    providerInfo.authority, LTContentProvider.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: LTContentProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        // content://com.nadajp.littletalkers/kids
        String type = mContext.getContentResolver().getType(DbContract.Kids.CONTENT_URI);
        // vnd.android.cursor.dir/com.nadajp.littletalkers/kids
        assertEquals("Error: the Kids CONTENT_URI should return Kids.CONTENT_TYPE",
                DbContract.Kids.CONTENT_TYPE, type);

        long testKidId = 1;
        // content://com.nadajp.littletalkers.kids/1
        type = mContext.getContentResolver().getType(
                DbContract.Kids.buildKidsUri(testKidId));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the Kids CONTENT_URI with kid ID should return Kids.CONTENT_ITEM_TYPE",
                DbContract.Kids.CONTENT_ITEM_TYPE, type);
    }
}
