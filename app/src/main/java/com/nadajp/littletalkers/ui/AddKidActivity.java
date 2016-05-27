package com.nadajp.littletalkers.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

   public void onKidAdded(int kidId) {
      Intent intent = new Intent(this, ItemListActivity.class);
      intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
      startActivity(intent);
      finish(); // unload this activity
   }
   
   public void onKidUpdated(int kidId) {
       Intent intent = new Intent(this, ManageKidsActivity.class);
       startActivity(intent);
       finish();
   }
}
