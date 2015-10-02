package com.nadajp.littletalkers;

import com.nadajp.littletalkers.utils.Prefs;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class SharedPrefsBackupAgent extends BackupAgentHelper 
{
   // The name of the SharedPreferences file
   static final String PREFS = Prefs.SHARED_PREFS_FILENAME;

   // A key to uniquely identify the set of backup data
   static final String PREFS_BACKUP_KEY = "prefs";

   // Allocate a helper and add it to the backup agent
   @Override
   public void onCreate() {
       SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PREFS);
       addHelper(PREFS_BACKUP_KEY, helper);
   }
}
