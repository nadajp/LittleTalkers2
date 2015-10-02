package com.nadajp.littletalkers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class AudioRecordFragment extends Fragment implements OnClickListener,
      OnErrorListener, OnInfoListener
{
   private static final String DEBUG_TAG = "AudioRecordFragment";

   private File mDirectory = null; // directory to store audio file
   private File mTempFile = null; // temporary audio file
   private MediaRecorder mRecorder; // audio recorder
   final static Animation mAnimation = new AlphaAnimation(1, 0); // Change alpha
                                                                 // from fully
                                                                 // visible to
                                                                 // invisible
   private ImageView mImgMic;
   private boolean mSecondRecording;
   private String mTempFileStem; // stem of the temporary filename (temp or tempQA)

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      View v = inflater.inflate(R.layout.fragment_audio_record, container,
            false);

      mImgMic = (ImageView) v.findViewById(R.id.button_mic);
      Bundle args = this.getArguments();
      mSecondRecording = args.getBoolean(Prefs.SECOND_RECORDING);
      mTempFileStem = args.getString(Prefs.TEMP_FILE_STEM);
      int type = args.getInt(Prefs.TYPE);
      if (type == Prefs.TYPE_QA)
      {
         v.setBackgroundColor(this.getResources().getColor(R.color.green));
         mImgMic.setImageDrawable(this.getResources().getDrawable(
               R.drawable.ic_circle_white_green_mic));
      }

      mImgMic.setOnClickListener(this);

      mAnimation.setDuration(500); // duration - half a second
      mAnimation.setInterpolator(new LinearInterpolator()); // do not alter
                                                            // animation rate
      mAnimation.setRepeatCount(Animation.INFINITE); // Repeat animation
                                                     // infinitely
      mAnimation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
                                                   // end so the button will
                                                   // fade back in

      // set directory for storing audio files
      String subdirectory = getString(R.string.app_name);
      mDirectory = Utils.getPublicDirectory(subdirectory, getActivity());
      startRecording();
      return v;
   }

   @Override
   public void onClick(View v)
   {
      stopRecording();
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState)
   {
      super.onActivityCreated(savedInstanceState);
   }

   @Override
   public void onInfo(MediaRecorder mr, int what, int extra)
   {
      String msg = getString(R.string.mediarecorder_error_msg);

      switch (what)
      {
      case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
         msg = getString(R.string.max_duration);
         break;
      case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
         msg = getString(R.string.max_size);
         break;
      }
      Toast.makeText(this.getActivity(), msg, Toast.LENGTH_LONG).show();
   }

   @Override
   public void onError(MediaRecorder mr, int what, int extra)
   {
      Toast.makeText(this.getActivity(), R.string.mediarecorder_error_msg,
            Toast.LENGTH_LONG).show();
   }

   private void startRecording()
   {
      if (this.getActivity().getPackageManager().hasSystemFeature(
            PackageManager.FEATURE_MICROPHONE) == false)
      {
         Toast.makeText(this.getActivity(), R.string.no_mic_available,
               Toast.LENGTH_LONG).show();
         Intent intent = new Intent();
         this.getActivity().setResult(Activity.RESULT_CANCELED, intent);
         this.getActivity().finish();
         return;
      }
      
      mImgMic.startAnimation(mAnimation);
      mRecorder = new MediaRecorder();
      mRecorder.setOnErrorListener(this);
      mRecorder.setOnInfoListener(this);

      if (mSecondRecording)
      {
         mTempFile = new File(mDirectory, mTempFileStem + "2.3gp");
      }
      else
      {
         mTempFile = new File(mDirectory, mTempFileStem + ".3gp");
      }
      mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      mRecorder.setOutputFile(mTempFile.getAbsolutePath());
      mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

      try
      {
         mRecorder.prepare();
         mRecorder.start();
      } catch (IOException e)
      {
         //Log.e(DEBUG_TAG, "Exception in preparing recorder: " + e.getMessage());
         Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_LONG)
               .show();
      }
   }

   private void stopRecording()
   {
      mImgMic.clearAnimation();
      Intent intent = new Intent();
      
      try
      {
         mRecorder.stop();
      } catch (Exception e)
      {
         //Log.w(getClass().getSimpleName(), "Exception in stopping recorder", e);
         this.getActivity().setResult(Activity.RESULT_CANCELED, intent);
         this.getActivity().finish();
         // can fail if start() failed for some reason
      }
      mRecorder.reset();
      this.getActivity().setResult(Activity.RESULT_OK, intent);
      this.getActivity().finish();
   }

   @Override
   public void onPause()
   {
      if (mRecorder != null)
      {
         mRecorder.release();
         mRecorder = null;
      }

      super.onPause();
   }

   @Override
   public void onDestroyView()
   {
      super.onDestroyView();
      mDirectory = null;
      mTempFile = null;
   }
}
