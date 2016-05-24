package com.nadajp.littletalkers.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class AddItemActivity extends BaseActivity implements AddItemFragment.OnAddNewPhraseListener {
    private static final String DEBUG_TAG = "AddItemActivity";

    AddItemPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    Toolbar mToolbar;
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.actionbar_tabs);
        super.onCreate(savedInstanceState);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new AddItemPagerAdapter(getFragmentManager(), this, super.getKidDefaults());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mType = tab.getPosition();
                Utils.setColor(mToolbar, mTabLayout, AddItemActivity.this, mType);
                Prefs.saveType(getApplicationContext(), mType);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mType = this.getIntent().getIntExtra(Prefs.TYPE, Prefs.TYPE_WORD);
        Log.i(DEBUG_TAG, "TYPE IS: " + mType);

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(Prefs.TYPE);
            invalidateOptionsMenu();
        }

        TabLayout.Tab tab = mTabLayout.getTabAt(mType);
        tab.select();
    }

    @Override
    public void changeKid(){
        setCurrentKidData(getKidDefaults());
    }

    protected void setCurrentKidData(Kid kid) {
        // update all tabs, even those that are not currently visible
        for (int i = 0; i < mSectionsPagerAdapter.registeredFragments.size(); i++) {
            AddItemFragment f = (AddItemFragment) mSectionsPagerAdapter.registeredFragments.get(i);
            if (f != null) {
                f.setKidDefaults(kid);
            }
        }
    }

    @Override
    public void onPhraseAdded(AddItemFragment fragment) {

    }

    public void onClickedShowDictionary(int kidId) {
        Prefs.saveKidId(this, mCurrentKidId);
        //Log.i(DEBUG_TAG, "Saved ID: " + kidId);
        Intent intent = new Intent(this, ItemListActivity.class);
        intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mType = Prefs.getType(this, Prefs.TYPE_WORD);
        intent.putExtra(Prefs.TYPE, mType);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentKidId < 0) {
            finish();
        }
    }
}