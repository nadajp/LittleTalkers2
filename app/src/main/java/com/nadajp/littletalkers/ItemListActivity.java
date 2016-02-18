package com.nadajp.littletalkers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class ItemListActivity extends BaseActivity {
    private static String DEBUG_TAG = "ItemListActivity";
    ItemListPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    //private Spinner mLanguageSpinner;
    //private boolean mbFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_item_list);

        //View v = inflateActivity(savedInstanceState);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ItemListPagerAdapter(getFragmentManager(), this, super.getKidDefaults());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabs.setupWithViewPager(mViewPager);

        // Give the TabLayout the ViewPager
        tabs.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        Utils.setColor(getSupportActionBar(), Utils.COLOR_BLUE, getApplicationContext());
                        break;
                    case 1:
                        Utils.setColor(getSupportActionBar(), Utils.COLOR_GREEN, getApplicationContext());
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


        if (this.getIntent().hasExtra(Prefs.TYPE)) {
            mType = this.getIntent().getIntExtra(Prefs.TYPE, Prefs.TYPE_WORD);
        } else {
            mType = Prefs.getType(this, Prefs.TYPE_WORD);
        }

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(Prefs.TYPE);
            invalidateOptionsMenu();
        }

        TabLayout.Tab tab = tabs.getTabAt(mType);
        tab.select();
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
