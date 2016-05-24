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

    // Constants
    private static String DEBUG_TAG = "Main Activity";
    public static final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 1;

    // Location services
    protected GoogleApiClient mGoogleApiClient;
    public static Location sLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to google api client, used to get location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Find out from shared preferences whether there are any kids yet
        int kidId = Prefs.getKidId(this, -1);

        if (kidId > 0) {
            // Go to ItemListActivity
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
            intent.putExtra(Prefs.ADD_TYPE, Prefs.TYPE_WORD);
            startActivity(intent);
        }
    }

    public void clickedAddKid() {
        Intent intent = new Intent(this, AddKidActivity.class);
        intent.putExtra(Prefs.CURRENT_KID_ID, -1);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(DEBUG_TAG, "Connected.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // Should we show an explanation?
            Log.i(DEBUG_TAG, "No location permission.");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_GET_LOCATION);
            }
            return;
        }
        sLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (sLastLocation != null) {
            Log.i(DEBUG_TAG, sLastLocation.getLatitude() + ", " + sLastLocation.getLongitude());
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
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    sLastLocation = null;
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(DEBUG_TAG, "Connection failed.");
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
