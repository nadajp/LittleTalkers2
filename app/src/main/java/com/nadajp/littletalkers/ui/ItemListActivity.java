package com.nadajp.littletalkers.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;

public class ItemListActivity extends BaseActivity implements
        ItemListFragment.OnListFragmentInteractionListener,
        ItemDetailFragment.OnAddNewPhraseListener {

    private static String DEBUG_TAG = "ItemListActivity";
    ItemListPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    Toolbar mToolbar;
    TabLayout mTabLayout;
    private boolean mTwoPane;
    private long mCurrentItemId;

    //private Spinner mLanguageSpinner;
    //private boolean mbFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_item_list);
        super.onCreate(savedInstanceState);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            Log.i(DEBUG_TAG, "Two Panes!");
        }

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mCurrentItemId = 0;

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ItemListPagerAdapter(getFragmentManager(), this, super.getKidDefaults());
        // Set up the ViewPager with the sections adapter.
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.dark_blue));
                        }
                        break;
                    case 1:
                        mToolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                        mTabLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.dark_green));
                        }
                        break;
                }
                if (mTwoPane && mType != tab.getPosition()) {
                    mCurrentItemId = 0;
                    insertDetailView(tab.getPosition());
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
            mCurrentItemId = savedInstanceState.getLong(Prefs.ITEM_ID);
            invalidateOptionsMenu();
        }

        TabLayout.Tab tab = mTabLayout.getTabAt(mType);
        tab.select();

        if (mTwoPane) {
            insertDetailView(mType);
        }
    }

    private void insertDetailView(int type) {
        Fragment fragment = ItemDetailFragment.newInstance(type);
        Bundle args = new Bundle();
        //Kid kid = super.getKidDefaults();
        Log.i(DEBUG_TAG, "Kid ID: " + mCurrentKidId);
        Log.i(DEBUG_TAG, "Kid: " + mKid.getName());
        args.putParcelable(getString(R.string.kid_details), mKid);
        args.putLong(ItemDetailFragment.ITEM_ID, mCurrentItemId);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
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
                mKid = getKidDetails();
                f.updateData(mKid);
            }
        }
        if (mTwoPane) {
            mCurrentItemId = 0;
            insertDetailView(mType);
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
        if (mTwoPane) {
            Log.i(DEBUG_TAG, "Two panes, replacing fragment");
            mCurrentItemId = 0;
            insertDetailView(mType);
        } else {
            Intent intent = new Intent(this, AddItemActivity.class);
            mType = Prefs.getType(this, Prefs.TYPE_WORD);
            intent.putExtra(Prefs.TYPE, mType);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Prefs.TYPE, mType);
        outState.putLong(Prefs.ITEM_ID, mCurrentItemId);
        mKid = getKidDetails();
    }

    @Override
    public void onListItemSelected(long id) {
        if (mTwoPane) {
            mCurrentItemId = id;
            insertDetailView(mType);
        } else {
            Log.i(DEBUG_TAG, "One pane, starting ViewItemActivity class");
            Intent intent = new Intent(this, ViewItemActivity.class);
            intent.putExtra(ItemDetailFragment.ITEM_ID, id);
            intent.putExtra(Prefs.TYPE, mType);
            intent.putExtra(Prefs.KID_NAME, mKid.getName());
            startActivity(intent);
        }
    }

    @Override
    public void onPhraseAdded(ItemDetailFragment fragment) {
        fragment.clearForm();
    }

    @Override
    public void onClickedShowDictionary(int kidId) {

    }
}
