package com.nadajp.littletalkers.sync;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.nadajp.littletalkers.AppConstants;
import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import java.util.List;

/**
 * Created by nadajp on 5/18/16.
 */
public class SetupSyncActivity extends AppCompatActivity {

    static final int REQUEST_ACCOUNT_PICKER = 2;
    public static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 5;
    private static String DEBUG_TAG = "SetupSync Activity";
    public GoogleAccountCredential mCredential;
    TextView mTextUpdateMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_sync);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Utils.insertWhiteUpArrow(getSupportActionBar(), this);

        mTextUpdateMessage = (TextView) findViewById(R.id.txtSyncUpdateMessage);

        // For API > 23, must ask for explicit permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.GET_ACCOUNTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupSync(null);
                } else {
                    // permission denied! Nothing left to do so finish the activity
                    mTextUpdateMessage.setText(getString(R.string.sync_permission_denied));
                    finish();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void setupSync(View v) {
        mCredential = GoogleAccountCredential
                .usingAudience(this, AppConstants.AUDIENCE)
                .setSelectedAccountName(Prefs.getGoogleAccountName(this));
        Log.i(DEBUG_TAG, "Account Name from credential: " + mCredential.getSelectedAccountName());
        if (mCredential.getSelectedAccountName() != null) {
            // Already signed in, begin app!
            mTextUpdateMessage.setText(getString(R.string.sync_setting_up));
            Log.i(DEBUG_TAG, "Already signed in, begin app...");
            SyncAdapter.initializeSyncAdapter(this);
        } else {
            // Not signed in, show login window or request an account.
            chooseAccount();
        }
    }

    // Set the chosen account name to preferences and add it to credential
    private void setSelectedGoogleAccountName(String accountName) {
        Prefs.saveGoogleAccountName(this, accountName);
        mCredential.setSelectedAccountName(accountName);
    }

    // Start a dialog that requests for the user to select a Google account
    void chooseAccount() {
        // Check if account picker is avaialable on this device
        Intent intent = mCredential.newChooseAccountIntent();
        PackageManager manager = getPackageManager();
        List<ResolveInfo> info = manager.queryIntentActivities(intent, 0);
        if (info.size() > 0) {
            startActivityForResult(mCredential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);
        } else {
            mTextUpdateMessage.setText(getString(R.string.google_sign_in_not_available));
        }

    }

    /* Function to handle the user choosing an account */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName =
                            data.getExtras().getString(
                                    AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        setSelectedGoogleAccountName(accountName);
                        SyncAdapter.initializeSyncAdapter(this);
                    }
                }
                break;
        }
    }
}
