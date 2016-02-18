package com.nadajp.littletalkers;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nadajp.littletalkers.ManageKidsFragment.ModifyKidsListener;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.utils.Prefs;

public class ManageKidsActivity extends AppCompatActivity implements ModifyKidsListener
{
   private static final String DEBUG_TAG = "ManageKidsActivity";

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_manage_kids);

      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle(R.string.title_activity_manage_kids);
      toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.manage_kids, menu);
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle presses on the action bar items
      switch (item.getItemId())
      {
      case R.id.action_add_kid:
         Intent intent = new Intent(this, AddKidActivity.class);
         startActivity(intent);
         finish();
         return true;
      case R.id.action_export:
         Intent backup_intent = new Intent(this, DataExportActivity.class);
         startActivity(backup_intent);
         finish();
         return true;
      default:
         return super.onOptionsItemSelected(item);
      }
   }

   @Override
   public void onKidsDeleted()
   {
      ContentResolver resolver = getContentResolver();
      String[] projection = new String[] { DbContract.Kids._ID };

      Cursor cursor = resolver.query(
              DbContract.Kids.CONTENT_URI, projection, null, null, null);

      if (!cursor.moveToNext())
      {
         Prefs.saveKidId(this, -1);
         Intent intent = new Intent(this, MainActivity.class);
         startActivity(intent);
         finish();
      }

      String sort = DbContract.Kids._ID + " DESC";
      cursor = resolver.query(DbContract.Kids.CONTENT_URI, projection, null, null, sort);

      cursor.moveToNext();
      int id = cursor.getInt(cursor.getColumnIndex(DbContract.Kids._ID));
      Prefs.saveKidId(this, id);
   }
}
