package com.nadajp.littletalkers.contentprovider;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.nadajp.littletalkers.database.DbContract;

/**
 * Created by nadajp on 6/30/15.
 */
/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final String KID_QUERY = "Leo";
    private static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    private static final int TEST_KID_ID = 1;

    // content://com.nadajp.littletalkers/kids"
    private static final Uri TEST_KIDS_DIR = DbContract.Kids.CONTENT_URI;
    private static final Uri TEST_WORDS_DIR = DbContract.Words.CONTENT_URI;
    private static final Uri TEST_QAS_DIR = DbContract.Questions.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = LTContentProvider.buildUriMatcher();

        assertEquals("Error: The KIDS URI was matched incorrectly.",
                testMatcher.match(TEST_KIDS_DIR), LTContentProvider.KIDS);
        assertEquals("Error: The WORDS URI was matched incorrectly.",
                testMatcher.match(TEST_WORDS_DIR), LTContentProvider.WORDS);
        assertEquals("Error: The QAS URI was matched incorrectly.",
                testMatcher.match(TEST_QAS_DIR), LTContentProvider.QAS);
    }
}
