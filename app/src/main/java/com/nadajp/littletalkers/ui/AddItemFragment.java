package com.nadajp.littletalkers.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public abstract class AddItemFragment extends ItemDetailFragment
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private static final String DEBUG_TAG = "AddItemFragment";
    private OnAddNewPhraseListener mListener; // listener to notify activity that a phrase has been added
    // Location services
    protected GoogleApiClient mGoogleApiClient;
    public static final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 1;
    public static Location sLastLocation;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        // Connect to google api client, used to get location
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        super.onActivityCreated(savedInstanceState);
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
            //Log.i(DEBUG_TAG, "Current city is default.");
        } /*else {
            Log.i(DEBUG_TAG, "Current city is now set to: " + mLocation);
        }*/
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
                    + " must implemenet AddItemFragment.OnAddNewPhraseListener");
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
                    + " must implemenet AddItemFragment.OnAddNewPhraseListener");
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
        Location location = sLastLocation;
        String currentCity = null;

        if (location != null) {
            Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
                //Log.i(DEBUG_TAG, e.getMessage());
            }
            if (addresses.size() > 0) {
                currentCity = addresses.get(0).getLocality();
                //Log.i(DEBUG_TAG, "Current City: " + currentCity);
            }
        }
        return currentCity;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.i(DEBUG_TAG, "Connected...");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Log.i(DEBUG_TAG, "No location permission.");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GET_LOCATION);
        } else {
            sLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                } else {
                    // permission denied so just set location to null and default will be used
                    sLastLocation = null;
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.e(DEBUG_TAG, "Connection failed.");
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
