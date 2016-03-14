package com.nadajp.littletalkers.backup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbContract.Kids;

//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.nadajp.littletalkers.kidendpoint.Kidendpoint;
//import com.nadajp.littletalkers.kidendpoint.model.Kid;

/**
 * Handle the transfer of data between a server and an app, using the Android
 * sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60; // * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    // Constants representing column positions from KID.
    public static final int KIDS_COLUMN_ID = 0;
    public static final int KIDS_COLUMN_NAME = 1;
    public static final int KIDS_COLUMN_BIRTHDATE = 2;
    public static final int KIDS_COLUMN_LOCATION = 3;
    public static final int KIDS_COLUMN_LANGUAGE = 4;
    public static final int KIDS_COLUMN_PICTURE_URI = 5;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000; // 15 seconds
    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000; // 10 seconds
    private static final String[] KIDS_PROJECTION = new String[]{
            Kids._ID,
            Kids.COLUMN_NAME_NAME,
            Kids.COLUMN_NAME_BIRTHDATE_MILLIS,
            Kids.COLUMN_NAME_DEFAULT_LOCATION,
            Kids.COLUMN_NAME_DEFAULT_LANGUAGE,
            Kids.COLUMN_NAME_PICTURE_URI};
    private static final String DEBUG_TAG = "SyncAdapter";
    // Global variables
    //private GoogleAccountCredential mCredential;
    public String mAccountName;
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.i(DEBUG_TAG, "Entering SyncAdapter...");
      /*
       * If your app uses a content resolver, get an instance of it from the
       * incoming Context
       */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the constructor maintains
     * compatibility with Android 3.0 and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
      /*
       * If your app uses a content resolver, get an instance of it from the
       * incoming Context
       */
        Log.i(DEBUG_TAG, "Entering SyncAdapter...");
        mContentResolver = context.getContentResolver();
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = DbContract.AUTHORITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.i("SyncAdapter", "syncImmediately...");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                DbContract.AUTHORITY, bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, DbContract.AUTHORITY, true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /*
     * Specify the code you want to run in the sync adapter. The entire sync
     * adapter runs in a background thread, so you don't have to set up your own
     * background processing.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
      /*
       * Put the data transfer code here.
       */
        Log.i(DEBUG_TAG, "Performing Sync!");

     /* mCredential = GoogleAccountCredential
            .usingAudience(this.getContext(), AppConstants.AUDIENCE);
      mCredential.setSelectedAccountName(Prefs.getAccountName(this.getContext()));*

      Littletalkersapi.Builder builder = new Littletalkersapi.Builder(
            AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
            mCredential);
      builder.setApplicationName(this.getContext().getString(R.string.app_name));
      Littletalkersapi ltEndpoint = builder.build();

      Long userId = Prefs.getUserId(this.getContext());
      Log.i(DEBUG_TAG, "user id from Prefs: " + userId);
      UserProfile profile = null;
      try
      {
         profile = ltEndpoint.getProfileById(userId).execute();
      } catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return;
      }
      Log.i(DEBUG_TAG, "userId: " + profile.getId() + "  email: " + profile.getEmail());
      if (profile.getId() != userId)
      {
         userId = profile.getId();
         Prefs.saveUserId(this.getContext(), userId);
      }

      // TODO: deletions

      UserDataWrapper data = ServerBackupUtils.getUserData();
      try {
         UserDataWrapper result = ltEndpoint.insertUserData(userId, data).execute();
      } catch (IOException e)
      {
         e.printStackTrace();
         return;
      }
      DbSingleton.get().setNotDirty(data);   */
    }

}