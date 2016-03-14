package com.nadajp.littletalkers.ui;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class ManageKidsActivity extends AppCompatActivity
        implements ManageKidsFragment.KidsListFragmentInteractionListener,
                   AddKidFragment.OnKidAddedListener {
    private static final String LOG_TAG = "ManageKidsActivity";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_kids);

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
            Log.i(LOG_TAG, "Two Panes!");
            insertDetailView(null);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utils.setColor(toolbar, null, this, Utils.COLOR_ORANGE);
        getSupportActionBar().setTitle(R.string.title_activity_manage_kids);
        Utils.insertWhiteUpArrow(getSupportActionBar(), this);
    }

    private void insertDetailView(Kid kid) {
        Fragment fragment = new AddKidFragment();

        Bundle args = new Bundle();
        if (kid != null) {
            args.putInt(Prefs.CURRENT_KID_ID, kid.getId());
            fragment.setArguments(args);
        }


        /*else {
            fragment = new KidProfileFragment();
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.kid_details), kid);
            fragment.setArguments(args);
        }*/

        getFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage_kids, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_export:
                Intent backup_intent = new Intent(this, DataExportActivity.class);
                startActivity(backup_intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addKid(View v) {
        if (mTwoPane) {
            insertDetailView(null);
        } else {
            Intent intent = new Intent(this, AddKidActivity.class);
            intent.putExtra(Prefs.CURRENT_KID_ID, -1);
            startActivity(intent);
        }
    }

    @Override
    public void onKidsDeleted() {
        ContentResolver resolver = getContentResolver();
        String[] projection = new String[]{DbContract.Kids._ID};

        Cursor cursor = resolver.query(
                DbContract.Kids.CONTENT_URI, projection, null, null, null);

        if (!cursor.moveToNext()) {
            Prefs.saveKidId(this, -1);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        String sort = DbContract.Kids._ID + " DESC";
        cursor = resolver.query(DbContract.Kids.CONTENT_URI, projection, null, null, sort);

        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndex(DbContract.Kids._ID));
        Prefs.saveKidId(this, id);
    }

    @Override
    public void onSelectKid(Kid kid) {
        Log.i(LOG_TAG, "Kid Selected! " + kid.getName());
        if (mTwoPane) {
            insertDetailView(kid);
        } else {
            Intent intent = new Intent(this, KidProfileActivity.class);
            intent.putExtra(getString(R.string.kid_details), kid);
            startActivity(intent);
        }
    }

    @Override
    public void onEditKid() {

    }

    @Override
    public void onKidAdded(int kidId) {

    }

    @Override
    public void onKidUpdated(int kidId) {

    }

    public void onEditProfile(int kidId) {
        if (!mTwoPane) {
            Intent intent = new Intent(this, AddKidActivity.class);
            intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
            startActivity(intent);
        } else {
            Fragment fragment = new AddKidFragment();
            Bundle args = new Bundle();
            args.putInt(Prefs.CURRENT_KID_ID, kidId);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }
    }


}
