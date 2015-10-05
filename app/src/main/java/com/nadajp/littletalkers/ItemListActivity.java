package com.nadajp.littletalkers;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class ItemListActivity extends BaseActivity implements ActionBar.TabListener {
    private static String DEBUG_TAG = "ItemListActivity";
    ItemListPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    //private Spinner mLanguageSpinner;
    //private boolean mbFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setLogo(android.R.color.transparent);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ItemListPagerAdapter(getFragmentManager(), this, super.getKidDefaults());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //mType = Prefs.getType(this, Prefs.TYPE_WORD);
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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

        if (this.getIntent().hasExtra(Prefs.TYPE)) {
            mType = this.getIntent().getIntExtra(Prefs.TYPE, Prefs.TYPE_WORD);
        } else {
            mType = Prefs.getType(this, Prefs.TYPE_WORD);
        }

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(Prefs.TYPE);
            invalidateOptionsMenu();
        }

        actionBar.setSelectedNavigationItem(mType);
    }

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
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    private ItemListFragment getCurrentFragment() {
        return (ItemListFragment) mSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_sort_alpha:
                sortByWord();
                return true;
            case R.id.action_sort_date:
                sortByDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
   
   /* Select language from filter dropdown
   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
   {
      if (!mbFilter)
      {
         return;
      }
      String language = parent.getItemAtPosition(pos).toString();
      for (int i = 0; i < mSectionsPagerAdapter.registeredFragments.size(); i++)
      {
         ItemListFragment f = (ItemListFragment) mSectionsPagerAdapter.registeredFragments.get(i);
         if (f != null) { f.changeLanguage(language); }
      }
   }

   public void onNothingSelected(AdapterView<?> parent)
   {
      // Another interface callback
   }*/

    @Override
    public void changeKid() {
        // update all tabs, even those that are not currently visible
        for (int i = 0; i < mSectionsPagerAdapter.registeredFragments.size(); i++) {
            ItemListFragment f = (ItemListFragment) mSectionsPagerAdapter.registeredFragments.get(i);
            if (f != null) {
                f.updateData(getKidDefaults());
            }
        }
    }

    public void sortByWord() {
        ItemListFragment listFragment = getCurrentFragment();
        if (listFragment != null) {
            listFragment.sort(Prefs.SORT_COLUMN_PHRASE);
        }
        Prefs.saveSortColumnId(this, Prefs.SORT_COLUMN_PHRASE);
    }

    public void sortByDate() {
        ItemListFragment listFragment = getCurrentFragment();
        if (listFragment != null) {
            listFragment.sort(Prefs.SORT_COLUMN_DATE);
        }
        Prefs.saveSortColumnId(this, Prefs.SORT_COLUMN_DATE);
    }

    public void addNewWord(View view) {
        Intent intent = new Intent(this, AddItemActivity.class);
        mType = Prefs.getType(this, Prefs.TYPE_WORD);
        intent.putExtra(Prefs.TYPE, mType);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Prefs.TYPE, mType);
    }
}
