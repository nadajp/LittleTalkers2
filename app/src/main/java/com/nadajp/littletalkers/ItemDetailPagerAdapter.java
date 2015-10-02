package com.nadajp.littletalkers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import com.nadajp.littletalkers.model.Kid;

public class ItemDetailPagerAdapter extends SectionsPagerAdapter
{
   Kid mKid;

   public ItemDetailPagerAdapter(FragmentManager fm, Context c, Kid kid)
   {
       super(fm, c);
       mKid = kid;
   }

   @Override
   public Fragment getItem(int position)
   {
       Fragment fragment = ItemDetailFragment.newInstance(position);
       Bundle args = new Bundle();
       args.putParcelable("Kid", mKid);
       fragment.setArguments(args);
       return fragment;
   }
}
