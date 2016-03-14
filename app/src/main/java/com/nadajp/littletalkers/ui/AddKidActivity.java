package com.nadajp.littletalkers.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class AddKidActivity extends AppCompatActivity implements
      AddKidFragment.OnKidAddedListener
{
   private static final String DEBUG_TAG = "AddKidActivity";
   private Toolbar mToolbar;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_kid);

      mToolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(mToolbar);
      getSupportActionBar().setDisplayShowTitleEnabled(true);

      if (getNumberOfKids() > 0) {
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }
      else {
         getSupportActionBar().setDisplayHomeAsUpEnabled(false);
      }

      Utils.setColor(mToolbar, null, this, Utils.COLOR_ORANGE);
   }

   private int getNumberOfKids() {
       Cursor cursor = null;
       try {
           cursor = getContentResolver().query(
                   DbContract.Kids.CONTENT_URI, null, null, null, null);
           return cursor.getCount();
       } finally {
           if (cursor != null) {
               cursor.close();
           }
       }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.add_kid, menu);
      /*MenuItem item = menu.findItem(R.id.action_add_word);
      mCurrentKidId = getIntent().getLongExtra(Prefs.CURRENT_KID_ID, -1);
      if (mCurrentKidId < 0)
      {
         item.setVisible(false);
      } else
      {
         item.setVisible(true);
      }*/
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle presses on the action bar items
      switch (item.getItemId()) {
      /*case R.id.action_add_word:
         switchToAddWord();
         return true;*/
      case R.id.action_manage_kids:
         Intent manage_intent = new Intent(this, ManageKidsActivity.class);
         startActivity(manage_intent);
         return true;
      case R.id.action_export:
         Intent backup_intent = new Intent(this, DataExportActivity.class);
         startActivity(backup_intent);
      default:
         return super.onOptionsItemSelected(item);
      }
   }

   public void onKidAdded(int kidId) {
      Intent intent = new Intent(this, ItemListActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
      // Log.i(DEBUG_TAG, "In onKidAdded, kid from Prefs is: " + Prefs.getKidId(this, -1));
      startActivity(intent);
      finish(); // unload this activity
   }
   
   public void onKidUpdated(int kidId) {
      Intent intent = new Intent(this, ManageKidsActivity.class);
      startActivity(intent);
   }
}
