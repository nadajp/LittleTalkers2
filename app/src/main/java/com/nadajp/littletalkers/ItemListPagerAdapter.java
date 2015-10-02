package com.nadajp.littletalkers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import com.nadajp.littletalkers.utils.Prefs;

public class ItemListPagerAdapter extends SectionsPagerAdapter
{
   private int mKidId;

   public ItemListPagerAdapter(FragmentManager fm, Context c, int kidId)
   {
      super(fm, c);
      mKidId = kidId;
   }

   @Override
   public Fragment getItem(int position)
   {
       Fragment fragment = ItemListFragment.newInstance(position);
       Bundle args = new Bundle();
       args.putInt(Prefs.CURRENT_KID_ID, mKidId);
       fragment.setArguments(args);
       return fragment;
   }
}
