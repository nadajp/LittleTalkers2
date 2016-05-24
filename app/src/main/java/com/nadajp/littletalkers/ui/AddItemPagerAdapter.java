package com.nadajp.littletalkers.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.model.Kid;

public class AddItemPagerAdapter extends SectionsPagerAdapter {
    Kid mKid;
    Context mContext;

    public AddItemPagerAdapter(FragmentManager fm, Context c, Kid kid) {
        super(fm, c);
        mKid = kid;
        mContext = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = AddItemFragment.newInstance(position);
        Bundle args = new Bundle();
        args.putParcelable(mContext.getString(R.string.kid_details), mKid);
        fragment.setArguments(args);
        return fragment;
    }
}
