package com.nadajp.littletalkers;

import com.nadajp.littletalkers.utils.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class KidProfileActivity extends Activity
{

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_kid_profile);

      if (savedInstanceState == null)
      {
         getFragmentManager().beginTransaction()
               .add(R.id.container, new KidProfileFragment()).commit();
      }
      ActionBar actionBar = this.getActionBar();
      Utils.setColor(actionBar, Utils.COLOR_ORANGE, this);
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
      actionBar.setTitle(R.string.title_activity_kid_profile);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
       switch (item.getItemId()) 
       {
       // Respond to the action bar's Up/Home button
       // in this case, return to referring activity
       case android.R.id.home:
           super.onBackPressed();
           return true;
       case R.id.action_export:
          Intent backup_intent = new Intent(this, DataExportActivity.class);
          startActivity(backup_intent);
          return true;
       case R.id.action_manage_kids:
          Intent manage_intent = new Intent(this, ManageKidsActivity.class);
          startActivity(manage_intent);
          return true;
       }
       return super.onOptionsItemSelected(item);
   }
}
