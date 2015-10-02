package com.nadajp.littletalkers.backup;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.backup.SyncNowFragment.OnFragmentInteractionListener;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SyncNowActivity extends Activity implements OnFragmentInteractionListener
{

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_sync_now);
      if (savedInstanceState == null)
      {
         getFragmentManager().beginTransaction().add(R.id.container, new SyncNowFragment()).commit();
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.sync_now, menu);
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

   /**
    * A placeholder fragment containing a simple view.
    */
   public static class PlaceholderFragment extends Fragment
   {

      public PlaceholderFragment()
      {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
      {
         View rootView = inflater.inflate(R.layout.fragment_sync_now,
               container, false);
         return rootView;
      }
   }

   @Override
   public void onFragmentInteraction(Uri uri)
   {
      // TODO Auto-generated method stub
      
   }
}
