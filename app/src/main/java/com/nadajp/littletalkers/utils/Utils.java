package com.nadajp.littletalkers.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static final String DEBUG_TAG = "Utils";

    public static void setColor(Toolbar toolbar, TabLayout tabLayout, AppCompatActivity activity, int color) {
        switch (color) {
            case COLOR_BLUE:
                toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.blue));
                if (tabLayout != null) {
                    tabLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.blue));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = activity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(activity, R.color.dark_blue));
                }
                break;
            case COLOR_GREEN:
                toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                if (tabLayout != null) {
                    tabLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = activity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(activity, R.color.dark_green));
                }
                break;
            case COLOR_ORANGE:
                toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.orange));
                if (tabLayout != null) {
                    tabLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.orange));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = activity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(activity, R.color.dark_orange));
                }
                break;
            case COLOR_RED:
                toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                if (tabLayout != null) {
                    tabLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = activity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(activity, R.color.dark_red));
                }
                break;
        }
    }

    public static void insertWhiteUpArrow(ActionBar actionBar, Context context) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
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
