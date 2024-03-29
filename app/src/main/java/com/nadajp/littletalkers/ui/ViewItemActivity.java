package com.nadajp.littletalkers.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class ViewItemActivity extends AppCompatActivity implements ViewItemFragment.OnUpdatePhraseListener {
    private static final String DEBUG_TAG = "ViewItemActivity";
    private int mType;
    private ViewItemFragment mFragment;
    private String mKidName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utils.insertWhiteUpArrow(getSupportActionBar(), this);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(Prefs.TYPE);
            mKidName = savedInstanceState.getString(Prefs.KID_NAME);
        } else {
            mType = getIntent().getIntExtra(Prefs.TYPE, Prefs.TYPE_WORD);
            mKidName = getIntent().getStringExtra(Prefs.KID_NAME);
            long itemId = getIntent().getLongExtra(ItemDetailFragment.ITEM_ID, 0);
            Log.i(DEBUG_TAG, "Kid name being passed to ViewItemFragment: " + mKidName);
            Log.i(DEBUG_TAG, "Item id being passed to ViewItemFragment: " + itemId);
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(ItemDetailFragment.ITEM_ID, itemId);
            arguments.putString(Prefs.KID_NAME, mKidName);

            if (mType == Prefs.TYPE_WORD) {
                mFragment = new ViewWordFragment();
            } else {
                mFragment = new ViewQAFragment();
            }
            mFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mFragment).commit();
        }
        Utils.setColor(toolbar, null, this, mType);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, ItemListActivity.class);
            mType = Prefs.getType(this, Prefs.TYPE_WORD);
            intent.putExtra(Prefs.TYPE, mType);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPhraseUpdated(int kidId) {
        Intent intent = new Intent(this, ItemListActivity.class);
        intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mType = Prefs.getType(this, Prefs.TYPE_WORD);
        intent.putExtra(Prefs.TYPE, mType);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Prefs.TYPE, mType);
        outState.putString(Prefs.KID_NAME, mKidName);
    }
}
