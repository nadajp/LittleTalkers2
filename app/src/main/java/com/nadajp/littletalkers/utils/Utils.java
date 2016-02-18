package com.nadajp.littletalkers.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
    public static final int COLOR_BLUE = 0;
    public static final int COLOR_GREEN = 1;
    public static final int COLOR_RED = 2;
    public static final int COLOR_ORANGE = 3;
    // The following are used in caching data changes for server upload
    public static final String ADD_WORD = "add_word";
    public static final String UPDATE_WORD = "update_word";
    public static final String ADD_QA = "add_qa";
    public static final String UPDATE_QA = "update_qa";
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static final String DEBUG_TAG = "Utils";

    public static void setColor(android.support.v7.widget.Toolbar actionBar, int color, Context context){
        switch (color) {
            case COLOR_BLUE:
                actionBar.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                break;
            case COLOR_GREEN:
                actionBar.setBackground(ContextCompat.getDrawable(context, R.drawable.ab_bottom_solid_littletalkersgreenstyle));
                break;
            case COLOR_RED:
                actionBar.setBackground(ContextCompat.getDrawable(context, R.drawable.ab_bottom_solid_littletalkersredstyle));
                break;
            case COLOR_ORANGE:
                actionBar.setBackground(ContextCompat.getDrawable(context, R.drawable.ab_bottom_solid_littletalkersorangestyle));
                break;
        }

    }

    public static void setColor(android.support.v7.app.ActionBar actionBar, int color, Context context) {
        switch (color) {
            case COLOR_BLUE:
                //actionBar.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));

                actionBar.setBackgroundDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ab_bottom_solid_littletalkersstyle));
                actionBar.setStackedBackgroundDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ab_stacked_solid_littletalkersstyle));

                break;
            case COLOR_GREEN:

                actionBar.setBackgroundDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ab_bottom_solid_littletalkersgreenstyle));
                actionBar.setStackedBackgroundDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ab_stacked_solid_littletalkersgreenstyle));
                //actionBar.setBackground(ContextCompat.getDrawable(context, R.drawable.ab_bottom_solid_littletalkersgreenstyle));
                break;
            case COLOR_RED:
                //actionBar.setBackground(ContextCompat.getDrawable(context, R.drawable.ab_bottom_solid_littletalkersredstyle));

                actionBar.setBackgroundDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ab_bottom_solid_littletalkersredstyle));
                actionBar.setStackedBackgroundDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ab_stacked_solid_littletalkersredstyle));
                break;
            case COLOR_ORANGE:
                actionBar.setBackgroundDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ab_bottom_solid_littletalkersorangestyle));
                actionBar.setStackedBackgroundDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ab_stacked_solid_littletalkersorangestyle));
                //actionBar.setBackground(ContextCompat.getDrawable(context, R.drawable.ab_bottom_solid_littletalkersorangestyle));
                break;
        }
    }

    public static String getDateForDisplay(String rawdate, Context context) {
        String[] dateArray = rawdate.split("-");
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
        date.set(Calendar.MONTH, Integer.parseInt(dateArray[1]));
        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));

        String formatted = DateUtils.formatDateTime(context,
                date.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_YEAR);
        return formatted;
    }

    public static String getDateForDisplay(long msDate, Context context) {
        String formatted = "";
        if (msDate == 0) {
            return formatted;
        }
        return DateUtils.formatDateTime(context, msDate,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    /**
     * Method to extract the user's age from the entered Date of Birth.
     *
     * @param DoB String The user's date of birth.
     * @return ageS String The user's age in years based on the supplied DoB.
     */
    public static String getAge(long birthdayInMillis) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTimeInMillis(birthdayInMillis);

        Integer age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        int current_month = today.get(Calendar.MONTH);
        int birth_month = dob.get(Calendar.MONTH);
        int birth_day_of_month = dob.get(Calendar.DAY_OF_MONTH);
        int current_day_of_month = today.get(Calendar.DAY_OF_MONTH);
        int months = 0;

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
            months = 12 - (birth_month - current_month);
            if (birth_month == current_month || current_day_of_month < birth_day_of_month) {
                months--;
            }
        } else {
            months = current_month - birth_month;
            if (current_day_of_month < birth_day_of_month) {
                months--;
            }
        }

        String ageString = age.toString() + " years, " + months + " months";

        return ageString;
    }

    public static File getPublicDirectory(String subdirectory, Context context) {
        String state = Environment.getExternalStorageState();
        File directory;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            directory = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    subdirectory);
        } else {
            directory = new File(context.getFilesDir(), subdirectory);
        }

        if (!directory.exists()) {
            directory.mkdir();
        }
        return directory;
    }

    /**
     * Generate a value suitable for use in {@link #setId(int)}. This value will
     * not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range
            // under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static File renameAudioFile(String phrase, String kidName,
                                       File audioFile, File directory, Calendar date) {
        String[] a = phrase.split(" ");
        StringBuffer str = new StringBuffer(a[0].trim());
        for (int i = 1; i < a.length; i++) {
            str.append(a[i].trim());
            if (i == 5) {
                break;
            }
        }
        String baseFilename = kidName + "-" + str + date.getTimeInMillis()
                + ".3gp";

        File newfile = new File(directory, baseFilename);

        if (newfile.exists()) {
            newfile.delete();
        }

        Log.i(DEBUG_TAG, "Oldfile: " + audioFile.getAbsolutePath());
        Log.i(DEBUG_TAG, "Newfile: " + newfile.getAbsolutePath());

        if (audioFile.renameTo(newfile)) {
            Log.i(DEBUG_TAG, "Rename succesful");
        } else {
            Log.i(DEBUG_TAG, "Rename failed");
        }

        audioFile.delete();
        audioFile = newfile;
        return audioFile;
    }

    public static int getLastKidAdded(ContentResolver cr) {
        Cursor cursor = null;
        try {
            cursor = cr.query(DbContract.Kids.CONTENT_URI.buildUpon().encodedQuery("limit=1").build(),
                    new String[]{DbContract.Kids._ID},
                    null,
                    null,
                    DbContract.Kids._ID + " DESC");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            }
            return -1;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }


}
