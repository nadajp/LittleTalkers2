package com.nadajp.littletalkers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.nadajp.littletalkers.ItemDetailFragment.OnAddNewPhraseListener;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;

public class AddItemActivity extends BaseActivity implements OnAddNewPhraseListener {
    private static final String DEBUG_TAG = "AddItemActivity";

    ItemDetailPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    Toolbar mToolbar;
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ItemDetailPagerAdapter(getFragmentManager(), this, super.getKidDefaults());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        mToolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.blue));
                        mTabLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.blue));
                        break;
                    case 1:
                        mToolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                        mTabLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                        break;
                }
                mType = tab.getPosition();
                Prefs.saveType(getApplicationContext(), tab.getPosition());
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
        /* When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.set(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
        mType = this.getIntent().getIntExtra(Prefs.TYPE, Prefs.TYPE_WORD);
        // Log.i(DEBUG_TAG, "TYPE IS: " + mType);

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(Prefs.TYPE);
            invalidateOptionsMenu();
        }
        actionBar.setSelectedNavigationItem(mType);
        */
    }

    @Override
    public void changeKid(){
        setCurrentKidData(getKidDefaults());
    }
/*
    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        int position = tab.getPosition();
        //Log.i(DEBUG_TAG, "CURRENT POSITION: " + position);
        mViewPager.setCurrentItem(position);
        ActionBar actionBar = getActionBar();

        switch (position) {
            case 0:
                Utils.setColor(actionBar, Utils.COLOR_BLUE, this);
                break;
            case 1:
                Utils.setColor(actionBar, Utils.COLOR_GREEN, this);
                break;
        }
        mType = position;
        Prefs.saveType(this, position);
        //Log.i(DEBUG_TAG, "Saving type: " + mType);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }
*/
    protected void setCurrentKidData(Kid kid) {
        // update all tabs, even those that are not currently visible
        for (int i = 0; i < mSectionsPagerAdapter.registeredFragments.size(); i++) {
            ItemDetailFragment f = (ItemDetailFragment) mSectionsPagerAdapter.registeredFragments.get(i);
            if (f != null) {
                f.setKidDefaults(kid);
            }
        }
    }

    public void onPhraseAdded(int kidId) {
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
