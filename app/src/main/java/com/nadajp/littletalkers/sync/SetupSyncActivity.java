package com.nadajp.littletalkers.sync;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.nadajp.littlealkers.backend.littleTalkersApi.LittleTalkersApi;
import com.nadajp.littlealkers.backend.littleTalkersApi.model.UserDataWrapper;
import com.nadajp.littlealkers.backend.littleTalkersApi.model.UserProfile;
import com.nadajp.littletalkers.AppConstants;
import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;

import java.io.IOException;

/**
 * Created by nadajp on 5/18/16.
 */
public class SetupSyncActivity extends AppCompatActivity {

    static final int REQUEST_ACCOUNT_PICKER = 2;
    public static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 5;
    private static String DEBUG_TAG = "SetupSync Activity";
    public GoogleAccountCredential mCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_sync);

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
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
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
            Log.i(DEBUG_TAG, "Already signed in, begin app...");
            SyncAdapter.initializeSyncAdapter(this);
        } else {
            // Not signed in, show login window or request an account.
            chooseAccount();
        }
    }

    // setSelectedAccountName definition
    private void setSelectedGoogleAccountName(String accountName) {
        Prefs.saveGoogleAccountName(this, accountName);
        mCredential.setSelectedAccountName(accountName);
    }

    void chooseAccount() {
        startActivityForResult(mCredential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

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

    class EndpointsAsyncTask extends AsyncTask<Pair<Context, GoogleAccountCredential>, Void, String> {
        private LittleTalkersApi myApiService = null;
        private Context context;
        private GoogleAccountCredential credential;

        @Override
        protected String doInBackground(Pair<Context, GoogleAccountCredential>... params) {
            context = params[0].first;
            credential = params[0].second;
            Log.i(DEBUG_TAG, "Credential: " + mCredential.getSelectedAccountName());
            if (myApiService == null) {  // Only do this once
                LittleTalkersApi.Builder builder = new LittleTalkersApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), mCredential)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl(context.getString(R.string.server_url))
                        .setApplicationName(context.getString(R.string.app_name));
                        /*.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });*/
                // end options for devappserver
                myApiService = builder.build();
                Log.i(DEBUG_TAG, "MyApiService created...");
            }

            Long userId = Prefs.getUserId(context);
            ContentResolver resolver = context.getContentResolver();
            UserDataWrapper data = SyncUtils.getUserData(resolver);
            Log.i(DEBUG_TAG, "user id from Prefs: " + userId);

            // First ever sync
            if (userId == -1) {
                try {
                    UserProfile result = myApiService.insertProfile().execute();
                    userId = result.getId();
                    Log.i(DEBUG_TAG, "User id: " + userId);
                    Prefs.saveUserId(context, userId);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(DEBUG_TAG, e.getMessage());
                    return e.getMessage();
                }
            }
            try {
                UserProfile profile = myApiService.getProfileById(userId).execute();
                Log.i(DEBUG_TAG, "userId: " + profile.getId() + "  email: " + profile.getEmail());
                if (profile.getId() != userId) {
                    userId = profile.getId();
                    Prefs.saveUserId(context, userId);
                }
                // TODO: deletions
                UserDataWrapper userData = myApiService.insertUserData(userId, data).execute();
                if (userData != null) {
                    SyncUtils.setNotDirty(userData, resolver);
                    return "data uploaded";
                }
                return "nothing to upload";
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(DEBUG_TAG, e.getMessage());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            Log.i(DEBUG_TAG, "Result: " + result);
        }
    }

}
