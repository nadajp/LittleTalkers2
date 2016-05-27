package com.nadajp.littletalkers.ui;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class AddItemFragment extends ItemDetailFragment {

    private static final String DEBUG_TAG = "AddItemFragment";
    private OnAddNewPhraseListener mListener; // listener to notify activity that a phrase has been added

    public abstract void saveToPrefs();

    public abstract void updateExtraKidDetails();

    public static android.app.Fragment newInstance(int sectionNumber) {
        Bundle args = new Bundle();
        args.putInt(Prefs.TAB_ID, sectionNumber);

        AddItemFragment fragment;
        switch (sectionNumber) {
            case 1:
                fragment = new AddQAFragment();
                break;
            default:
                fragment = new AddWordFragment();
                break;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_item, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected void insertData(View v) {
        // get Kid name and defaults to display
        Kid kid = this.getArguments().getParcelable(getString(R.string.kid_details));
        mKidName = kid.getName();
        mCurrentKidId = kid.getId();

        mLocation = getLocation();
        if (mLocation == null) {
            mLocation = kid.getLocation();
            Log.i(DEBUG_TAG, "Current city is default.");
        } else {
            Log.i(DEBUG_TAG, "Current city is now set to: " + mLocation);
        }
        mLanguage = kid.getLanguage();
        mEditLocation.setText(mLocation);
        mLangSpinner.setSelection(mLanguageAdapter.getPosition(mLanguage));
        if (mAudioRecorded) {
            mTempFile = new File(mDirectory, mTempFileStem + ".3gp");
            mTempFile2 = new File(mDirectory, mTempFileStem + "2.3gp");
        }
        updateExtraKidDetails();
    }

    public void setKidDefaults(Kid kid) {
        mCurrentKidId = kid.getId();
        mLanguage = kid.getLanguage();
        mLocation = kid.getLocation();
        mKidName = kid.getName();

        mLangSpinner.setSelection(mLanguageAdapter.getPosition(mLanguage));
        mEditLocation.setText(mLocation);
    }

    protected void saveItem(boolean exit) {
        super.saveItem(exit);
        if (mItemId > 0 && exit) {
            mListener.onClickedShowDictionary(mCurrentKidId);
        } else if (mItemId > 0) {
            mListener.onPhraseAdded(this);
        }
    }

    public void clearForm() {
        mEditPhrase.setText("");
        mDate = Calendar.getInstance();
        updateDate();
        mEditToWhom.setText("");
        mEditNotes.setText("");
        mRecordingLayout.setVisibility(View.GONE);
        mAudioRecorded = false;
        mEditPhrase.setError(null);
        clearExtraViews();
        mItemId = 0;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnAddNewPhraseListener) {
            mListener = (OnAddNewPhraseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implemenet ItemDetailFragment.OnAddNewPhraseListener");
        }
        super.onAttach(context);
    }

    // for API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnAddNewPhraseListener) {
            mListener = (OnAddNewPhraseListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet ItemDetailFragment.OnAddNewPhraseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAddNewPhraseListener {
        void onPhraseAdded(AddItemFragment fragment);
        void onClickedShowDictionary(int kidId);
    }

    private String getLocation() {
        Location location = MainActivity.sLastLocation;
        String currentCity = null;

        if (location != null) {
            Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(DEBUG_TAG, e.getMessage());
            }
            if (addresses.size() > 0) {
                currentCity = addresses.get(0).getLocality();
                Log.i(DEBUG_TAG, "Current City: " + currentCity);
            }
        }
        return currentCity;
    }
}
