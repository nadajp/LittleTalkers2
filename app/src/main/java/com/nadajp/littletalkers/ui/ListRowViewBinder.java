package com.nadajp.littletalkers.ui;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.utils.Utils;

import java.io.IOException;

public class ListRowViewBinder implements ViewBinder
{
   private static final String DEBUG_TAG = "ListRowViewBinder";
   private MediaPlayer mPlayer;
   final static Animation mAnimation = new AlphaAnimation(1, 0); // Change alpha
   static final int COL_ID = 0;
   static final int COL_WORD = 1;
   static final int COL_DATE = 2;
   static final int COL_AUDIO = 3;

   private View mPlayButton; // the view that holds currently pressed (animated)
                             // button

   public ListRowViewBinder(MediaPlayer mediaPlayer)
   {
      mPlayer = mediaPlayer;
      mAnimation.setDuration(500); // duration - half a second
      mAnimation.setInterpolator(new LinearInterpolator()); // do not alter
                                                            // animation rate
      mAnimation.setRepeatCount(Animation.INFINITE); // Repeat animation
                                                     // infinitely
      mAnimation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
                                                   // end so the button will
                                                   // fade back in
      mPlayButton = null;
   }

   public void setMediaPlayer(MediaPlayer mediaPlayer)
   {
      mPlayer = mediaPlayer;
   }

   @Override
   public boolean setViewValue(View view, Cursor cursor, int columnIndex)
   {
      if (columnIndex == cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_DATE))
      {
         long rawdate = cursor.getLong(columnIndex);
         String formatted = Utils.getDateForDisplay(rawdate, view.getContext());
         TextView txt = (TextView) view;
         txt.setText(formatted);
         return true;
      }

      else if (columnIndex == cursor
            .getColumnIndex(DbContract.Words.COLUMN_NAME_AUDIO_FILE))
      {
         // If the column is COLUMN_NAME_AUDIO_FILE then we use custom view.
         String audioFile = cursor.getString(columnIndex);

         if (audioFile == null || audioFile.isEmpty())
         {
            view.setVisibility(View.INVISIBLE);
         }

         else
         {
            view.setFocusable(false);
            view.setFocusableInTouchMode(false);
            // set the visibility of the view to visible
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new MyListener(audioFile));
         }
         return true;
      }
      // For others, we simply return false so that the default binding happens.
      return false;
   }

   private class MyListener implements OnClickListener, OnCompletionListener
   {
      private String mAudioFile;

      public MyListener(String audioFile)
      {
         this.mAudioFile = audioFile;
      }

      @Override
      public void onClick(View v)
      {
         if (mPlayer.isPlaying())
         {
            Stop();
            // return;
         }
         mPlayButton = v;
         v.setPressed(true);
         v.startAnimation(mAnimation);
         try
         {
            mPlayer.setDataSource(mAudioFile);
            mPlayer.setOnCompletionListener(this);
            //Log.i(DEBUG_TAG, "Started Playing " + mAudioFile);
            mPlayer.prepare();
            mPlayer.start();
         } catch (IOException e)
         {
            //Log.e(DEBUG_TAG, "Audio player start failed");
         }
      }

      public void onCompletion(MediaPlayer mp)
      {
         Stop();
      }

      public void Stop()
      {
         mPlayer.stop();
         mPlayer.reset();
         mPlayButton.setPressed(false);
         mPlayButton.clearAnimation();
      }
   }
}
