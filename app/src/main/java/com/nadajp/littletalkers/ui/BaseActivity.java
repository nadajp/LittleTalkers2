package com.nadajp.littletalkers.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.sync.SetupSyncActivity;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/*
 * This class is the base for add/view item and dictionary activities
 * It handles the action bar, including kids dropdown and common menu items
 */
public abstract class BaseActivity extends AppCompatActivity implements OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>  {

    static final String[] KID_DETAILS_PROJECTION = new String[]{
            Kids._ID,
            Kids.COLUMN_NAME_NAME,
    };

    private static final String DEBUG_TAG = "BaseActivity";
    private static final int KIDS_LOADER = 1;
    protected SimpleCursorAdapter mCursorAdapter = null;
    protected int mCurrentKidId;
    protected String mCurrentKidName;
    protected int mType;
    private int mPosition;
    private CircularImageView mImgProfile;
    private Spinner mSpinner;
    private int mLayoutResId;
    protected Kid mKid;

    // When a different kid is selected from dropdown, derived classes
    // handle it appropriately through this function
    public abstract void changeKid();

    public void setLayout(int resId) {
        mLayoutResId = resId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(mLayoutResId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(android.R.color.transparent);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mImgProfile = (CircularImageView) toolbar.findViewById(R.id.action_profile);
        mSpinner = (Spinner) toolbar.findViewById(R.id.action_main_spinner);

        if (savedInstanceState != null) {
            mCurrentKidId = savedInstanceState.getInt(Prefs.CURRENT_KID_ID);
            mPosition = savedInstanceState.getInt(Prefs.POSITION);
        } else {
            mCurrentKidId = Prefs.getKidId(this, Utils.getLastKidAdded(getContentResolver()));
            mPosition = -1;
        }
        mKid = getKidDetails();
        Log.i(DEBUG_TAG, "Kid ID: " + mCurrentKidId);

        // set up empty adapter
        String[] adapterCols = new String[]{"name"};
        int[] adapterRowViews = new int[]{android.R.id.text1};

        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.kid_spinner_item,
                null, adapterCols, adapterRowViews, 0);
        mCursorAdapter
                .setDropDownViewResource(R.layout.kid_spinner_dropdown_item);
        mSpinner.setAdapter(mCursorAdapter);
        mSpinner.setOnItemSelectedListener(this);
        getLoaderManager().initLoader(KIDS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void changeProfilePic() {
        String pictureUri = getPicturePath(mCurrentKidId);
        Bitmap profilePicture = null;
        Log.i(DEBUG_TAG, "Profile pic uri: " + pictureUri);
        if (pictureUri == null) {
            profilePicture = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.profile);
        } else {
            profilePicture = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureUri),
                    80, 80);
        }
        mImgProfile.setImageBitmap(profilePicture);
    }

    protected Kid getKidDefaults() {
        Kid kid = null;
        String[] projection = new String[]{DbContract.Kids.COLUMN_NAME_NAME,
                DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE,
                DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION};

        Cursor cursor = getContentResolver().query(
                DbContract.Kids.buildKidsUri(mCurrentKidId),
                projection,
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            String kidName = cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME));
            //Log.i(DEBUG_TAG, mKidName);
            String language = cursor.getString(cursor
                    .getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE));
            String location = cursor.getString(cursor
                    .getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION));

            kid = new Kid(mCurrentKidId, kidName, location, language);
        }
        return kid;
    }

    protected Kid getKidDetails() {
        Kid kid = null;
        Cursor cursor = getContentResolver().query(
                DbContract.Kids.buildKidsUri(mCurrentKidId),
                null,
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            String kidName = cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME));
            //Log.i(DEBUG_TAG, mKidName);
            String language = cursor.getString(cursor
                    .getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE));
            String location = cursor.getString(cursor
                    .getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION));
            String pictureUri = cursor.getString(cursor.getColumnIndex(Kids.COLUMN_NAME_PICTURE_URI));
            long birthday = cursor.getLong(cursor.getColumnIndex(Kids.COLUMN_NAME_BIRTHDATE_MILLIS));

            kid = new Kid(mCurrentKidId, kidName, location, language, pictureUri, birthday);
            mCurrentKidName = kidName;
        }
        return kid;
    }

    public void clickProfile(View v) {
        Intent intent = new Intent(this, KidProfileActivity.class);
        intent.putExtra(getString(R.string.kid_details), getKidDetails());
        startActivity(intent);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (mCurrentKidId == id) {
            return;
        }
        Log.i(DEBUG_TAG, "Selected kid with ID " + id + ", setting current kid...");
        mCurrentKidId = (int) id;
        mPosition = pos;
        mKid = getKidDetails();
        changeProfilePic();
        Prefs.saveKidId(this, mCurrentKidId);
        changeKid();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_export:
                Intent backup_intent = new Intent(this, DataExportActivity.class);
                startActivity(backup_intent);
                return true;
            case R.id.action_manage_kids:
                Intent manage_intent = new Intent(this, ManageKidsActivity.class);
                startActivity(manage_intent);
                return true;
            case R.id.action_dictionary:
                Intent dict_intent = new Intent(this, ItemListActivity.class);
                Prefs.saveType(this, mType);
                dict_intent.putExtra(Prefs.TYPE, mType);
                startActivity(dict_intent);
                finish();
                return true;
            case R.id.action_sync:
                Intent syncIntent = new Intent(this, SetupSyncActivity.class);
                startActivity(syncIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Prefs.POSITION, mPosition);
        outState.putInt(Prefs.CURRENT_KID_ID, mCurrentKidId);
        mType = Prefs.getType(this, Prefs.TYPE_WORD);
        outState.putInt(Prefs.TYPE, mType);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mPosition = savedInstanceState.getInt(Prefs.POSITION);
        mCurrentKidId = savedInstanceState.getInt(Prefs.CURRENT_KID_ID);
        mKid = getKidDetails();
        mType = savedInstanceState.getInt(Prefs.TYPE);
        //Log.i(DEBUG_TAG, "Restoring Type: " + mType);
    }

    private String getPicturePath(int id) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().
                    query(DbContract.Kids.buildKidsUri(id),
                            new String[]{DbContract.Kids.COLUMN_NAME_PICTURE_URI},
                            null,
                            null,
                            null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getString(0);
            }
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    @Override
    protected void onResume() {
        //mCurrentKidId = Prefs.getKidId(this, -1);
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //exportDB();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        CursorLoader loader = new CursorLoader(this, Kids.CONTENT_URI, KID_DETAILS_PROJECTION,
                null, null, null);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        changeProfilePic();

        if (mPosition >= 0 && mPosition < data.getCount()) {
            //Log.i(DEBUG_TAG, "Setting position: " + mPosition);
            mSpinner.setSelection(mPosition);
        } else {
            for (int i = 0; i < mCursorAdapter.getCount(); i++) {
                if (mCursorAdapter.getItemId(i) == mCurrentKidId) {
                    mSpinner.setSelection(i);
                    return;
                }
            }
            mSpinner.setSelection(0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    public void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            Log.i("DEBUG_TAG", "Trying to export DB");

            if (sd.exists() && sd.canWrite()) {
                Log.i("DEBUG_TAG", "Can write db");

                String currentDBPath = "/data/data/" + getPackageName()
                        + "/databases/littletalkers_db";
                Log.i(DEBUG_TAG, "currentDBPath = " + currentDBPath);
                String backupDBPath = "LTbackup.db";
                String subdirectory = getString(R.string.app_name);

                File currentDB = new File(currentDBPath);
                //File backupDB = new File(sd, backupDBPath);
                File backupDB = new File(Utils.getPublicDirectory(subdirectory, this), backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    MediaScannerConnection.scanFile(this,
                            new String[]{backupDB.getAbsolutePath()}, null, null);
                    //Intent mediaScannerIntent = new
                    // Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    // Uri fileContentUri = Uri.fromFile(backupDB); // With
                    // 'permFile' being the File object
                    // mediaScannerIntent.setData(fileContentUri);
                    // this.sendBroadcast(mediaScannerIntent); //

                    Log.i("DEBUG_TAG",
                            "DB exported to " + backupDB.getAbsolutePath());
                } else {
                    Log.i("DEBUG_TAG", "DB does not exist!");
                }
            }
        } catch (Exception e) {
            Log.i(DEBUG_TAG, "Could not export DB: " + e.getMessage());
        }
    }
}