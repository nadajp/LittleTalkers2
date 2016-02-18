package com.nadajp.littletalkers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class KidProfileActivity extends AppCompatActivity
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

       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
       getSupportActionBar().setStackedBackgroundDrawable(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.ab_stacked_solid_littletalkersorangestyle));
       getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.ab_bottom_solid_littletalkersorangestyle));
       getSupportActionBar().setDisplayShowHomeEnabled(true);
       getSupportActionBar().setTitle(R.string.title_activity_kid_profile);
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
