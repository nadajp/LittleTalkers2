package com.nadajp.littletalkers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Locale;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public abstract class SectionsPagerAdapter extends FragmentPagerAdapter
{
   private Context mCtxt;
   SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

   SectionsPagerAdapter(FragmentManager fm)
   {
      super(fm);
   }
   
   /** Constructor of the class */
   public SectionsPagerAdapter(FragmentManager fm, Context c) 
   {
       super(fm);
       mCtxt = c;
   }

   @Override
   public int getCount()
   {
      // Show 3 total pages.
      return 2;
   }

   @Override
   public CharSequence getPageTitle(int position)
   {
      Locale l = Locale.getDefault();
      switch (position)
      {
        case 0:
          return mCtxt.getString(R.string.word_or_phrase).toUpperCase(l);
        case 1:
          return mCtxt.getString(R.string.q_and_a).toUpperCase(l);
      }
      return null;
   }
   
   @Override
   public Object instantiateItem(ViewGroup container, int position)
   {
      Fragment fragment = (Fragment) super.instantiateItem(container, position);
      registeredFragments.put(position, fragment);
      return fragment;
   }
   
   @Override
   public void destroyItem(ViewGroup container, int position, Object object)
   {
      registeredFragments.remove(position);
      super.destroyItem(container, position, object);
   }
   
   public Fragment getRegisteredFragment(int position)
   {
      return registeredFragments.get(position);
   }
}
