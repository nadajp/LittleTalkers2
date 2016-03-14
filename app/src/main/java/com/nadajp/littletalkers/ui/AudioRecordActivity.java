package com.nadajp.littletalkers.ui;

import android.app.Activity;
import android.os.Bundle;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;


public class AudioRecordActivity extends Activity
{
   private int mType;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_audio_record);

      mType = this.getIntent().getExtras().getInt(Prefs.TYPE);
      String tempFileStem = this.getIntent().getExtras().getString(Prefs.TEMP_FILE_STEM);
      boolean secondRecording = this.getIntent().getExtras().getBoolean(Prefs.SECOND_RECORDING);
      AudioRecordFragment fragment = new AudioRecordFragment();
      Bundle args = new Bundle();
      args.putInt(Prefs.TYPE, mType);
      args.putBoolean(Prefs.SECOND_RECORDING, secondRecording);
      args.putString(Prefs.TEMP_FILE_STEM, tempFileStem);
      fragment.setArguments(args);
      if (savedInstanceState == null) {
         getFragmentManager().beginTransaction()
               .add(R.id.container, fragment).commit();
      }
   }
}
