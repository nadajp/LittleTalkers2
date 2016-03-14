package com.nadajp.littletalkers.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;

public class ItemListPagerAdapter extends SectionsPagerAdapter
{
   private Kid mKid;

   public ItemListPagerAdapter(FragmentManager fm, Context c, Kid kid)
   {
      super(fm, c);
      mKid = kid;
   }

   @Override
   public Fragment getItem(int position)
   {
       Fragment fragment = ItemListFragment.newInstance(position);
       Bundle args = new Bundle();
       args.putInt(Prefs.CURRENT_KID_ID, mKid.getId());
       args.putString(Prefs.KID_NAME, mKid.getName());
       fragment.setArguments(args);
       return fragment;
   }
}
