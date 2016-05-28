package com.nadajp.littletalkers.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;

public class MainActivity extends Activity implements MainFragment.AddKidListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 1;
    public static Location sLastLocation;
    // Constants
    private static String DEBUG_TAG = "Main Activity";
    // Location services
    protected GoogleApiClient mGoogleApiClient;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mType = -1;
        if (getIntent().hasExtra(Prefs.TYPE)) {
            mType = getIntent().getIntExtra(Prefs.TYPE, -1);
        }

        // Connect to google api client, used to get location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Find out from shared preferences whether there are any kids yet
        int kidId = Prefs.getKidId(this, -1);

        if (kidId > 0) {
            // unless coming from widget, type from intent will be -1 so default to Word
            mType = mType == -1 ? Prefs.TYPE_WORD : mType;

            // on tablet, go to ItemListActivity
            if (getResources().getBoolean(R.bool.two_pane)) {
                Intent intent = new Intent(this, ItemListActivity.class);
                intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
                intent.putExtra(Prefs.TYPE, mType);
                startActivity(intent);
                finish();
            } else { //on phone, go to AddItemActivity
                Intent intent = new Intent(this, AddItemActivity.class);
                intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
                intent.putExtra(Prefs.TYPE, mType);
                startActivity(intent);
                finish();
            }
        }
    }

    public void clickedAddKid() {
        Intent intent = new Intent(this, AddKidActivity.class);
        intent.putExtra(Prefs.CURRENT_KID_ID, -1);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Log.i(DEBUG_TAG, "No location permission.");
            ActivityCompat.requestPermissions(this,
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
