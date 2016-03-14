package com.nadajp.littletalkers.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.AccountPicker;
import com.nadajp.littletalkers.R;

public class SettingsActivity extends AppCompatActivity
{
   private static final String DEBUG_TAG = "SettingsActivity";
   static final int REQUEST_ACCOUNT_PICKER = 1;
   
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);

       if (savedInstanceState == null)
       {
          getFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment()).commit();
       }

       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
       this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.settings, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();
      if (id == R.id.action_settings)
      {
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

  /* @Override
   public void onFragmentInteraction(Uri uri)
   {
      // TODO Auto-generated method stub
      
   }  */

   // used in endpoints, this allows user to select account
   void chooseAccount()
   {
      startActivityForResult(AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                      false, null, null, null, null),
            REQUEST_ACCOUNT_PICKER);
   }

}
