package com.nadajp.littletalkers.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.sync.SetupSyncActivity;
import com.nadajp.littletalkers.utils.Utils;

public class KidProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid_profile);

        if (savedInstanceState == null) {

            Kid kid = getIntent().getParcelableExtra(getString(R.string.kid_details));
            Bundle args = new Bundle();
            args.putParcelable(getString(R.string.kid_details), kid);
            KidProfileFragment fragment = new KidProfileFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utils.setColor(toolbar, null, this, Utils.COLOR_RED);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_kid_profile);
        Utils.insertWhiteUpArrow(getSupportActionBar(), this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            // in this case, return to referring activity
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_export:
                Intent backup_intent = new Intent(this, DataExportActivity.class);
                startActivity(backup_intent);
                return true;
            case R.id.action_manage_kids:
                Intent manage_intent = new Intent(this, ManageKidsActivity.class);
                startActivity(manage_intent);
                return true;
            case R.id.action_sync:
                Intent syncIntent = new Intent(this, SetupSyncActivity.class);
                startActivity(syncIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
