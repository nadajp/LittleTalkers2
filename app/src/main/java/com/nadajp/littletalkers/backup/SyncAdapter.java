package com.nadajp.littletalkers.backup;

import java.io.IOException;
import java.util.ArrayList;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nadajp.littletalkers.AppConstants;
import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.database.DbContract.Words;
import com.nadajp.littletalkers.database.DbContract.Questions;
//import com.nadajp.littletalkers.kidendpoint.Kidendpoint;
//import com.nadajp.littletalkers.kidendpoint.model.Kid;


import com.nadajp.littletalkers.server.littletalkersapi.Littletalkersapi;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserDataWrapper;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserProfile;
import com.nadajp.littletalkers.utils.Prefs;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Handle the transfer of data between a server and an app, using the Android
 * sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter
{

   // Global variables
   private GoogleAccountCredential mCredential;
   public String mAccountName;
   
   // Define a variable to contain a content resolver instance
   ContentResolver mContentResolver;

   /**
    * Network connection timeout, in milliseconds.
    */
   private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000; // 15 seconds
   /**
    * Network read timeout, in milliseconds.
    */
   private static final int NET_READ_TIMEOUT_MILLIS = 10000; // 10 seconds

   private static final String[] KIDS_PROJECTION = new String[] { 
         Kids._ID,
         Kids.COLUMN_NAME_NAME,
         Kids.COLUMN_NAME_BIRTHDATE_MILLIS,
         Kids.COLUMN_NAME_DEFAULT_LOCATION,
         Kids.COLUMN_NAME_DEFAULT_LANGUAGE,
         Kids.COLUMN_NAME_PICTURE_URI};

   // Constants representing column positions from KID.
   public static final int KIDS_COLUMN_ID = 0;
   public static final int KIDS_COLUMN_NAME = 1;
   public static final int KIDS_COLUMN_BIRTHDATE = 2;
   public static final int KIDS_COLUMN_LOCATION = 3;
   public static final int KIDS_COLUMN_LANGUAGE = 4;
   public static final int KIDS_COLUMN_PICTURE_URI = 5;

   private static final String DEBUG_TAG = "SyncAdapter";

   /**
    * Set up the sync adapter
    */
   public SyncAdapter(Context context, boolean autoInitialize)
   {
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
         boolean allowParallelSyncs)
   {
      super(context, autoInitialize, allowParallelSyncs);
      /*
       * If your app uses a content resolver, get an instance of it from the
       * incoming Context
       */
      Log.i(DEBUG_TAG, "Entering SyncAdapter...");
      mContentResolver = context.getContentResolver();
      
   }

   /*
    * Specify the code you want to run in the sync adapter. The entire sync
    * adapter runs in a background thread, so you don't have to set up your own
    * background processing.
    */
   @Override
   public void onPerformSync(Account account, Bundle extras, String authority,
         ContentProviderClient provider, SyncResult syncResult)
   {
      /*
       * Put the data transfer code here.
       */      
      Log.i(DEBUG_TAG, "Performing Sync!");

      mCredential = GoogleAccountCredential
            .usingAudience(this.getContext(), AppConstants.AUDIENCE);
      mCredential.setSelectedAccountName(Prefs.getAccountName(this.getContext()));
      
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
      DbSingleton.get().setNotDirty(data);
      
     }
}