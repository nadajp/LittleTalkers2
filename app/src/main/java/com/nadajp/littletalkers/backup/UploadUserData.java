package com.nadajp.littletalkers.backup;

import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.database.DbSingleton;
import com.nadajp.littletalkers.server.littletalkersapi.Littletalkersapi;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserDataWrapper;
import com.nadajp.littletalkers.server.littletalkersapi.model.UserProfile;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;

/*
 * Upload all data for this user to the cloud. This will only be called
 * once, when the user signs up for the service and launches it
 */
public class UploadUserData extends AsyncTask<Context, Integer, Long>
{
   private static final String DEBUG_TAG = "UploadUserData";
   private GoogleAccountCredential mCredential;

   public UploadUserData(GoogleAccountCredential credential)
   {
      super();
      mCredential = credential;
   }

   protected Long doInBackground(Context... contexts)
   {
      UserDataWrapper data = ServerBackupUtils.getUserData();       

      Littletalkersapi.Builder builder = new Littletalkersapi.Builder(
               AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
               mCredential);
      Littletalkersapi ltEndpoint = builder.build();
     
      try
      {
         UserProfile result = ltEndpoint.insertProfile().execute();
         Long userId = result.getId();
         Log.i(DEBUG_TAG, "User id: " + userId);
         Prefs.saveUserId(contexts[0], userId);
            
         UserDataWrapper wrapper = ltEndpoint.insertUserData(userId, data).execute();
         Log.i(DEBUG_TAG, wrapper.getKids().get(0).getName());
         
      } catch (IOException e)
      {
         e.printStackTrace();
      }
      DbSingleton.get().setNotDirty(data);
      return (long) 0;
   }
   
   protected void onProgressUpdate(Integer... progress) {
      //setProgressPercent(progress[0]);
  }

  protected void onPostExecute(Long result) {
     //DbSingleton.get().setNotDirty(data);
  }
}
