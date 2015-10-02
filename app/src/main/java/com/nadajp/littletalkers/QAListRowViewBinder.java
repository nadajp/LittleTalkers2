package com.nadajp.littletalkers;

import java.io.IOException;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.utils.Utils;

public class QAListRowViewBinder implements ViewBinder
{
   private static final String DEBUG_TAG = "QAListRowViewBinder";
   private MediaPlayer mPlayer;

   public QAListRowViewBinder(MediaPlayer mediaPlayer)
   {
      mPlayer = mediaPlayer;
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
         String mAudioFile = cursor.getString(columnIndex);

         if (mAudioFile.isEmpty())
         {
            view.setVisibility(View.INVISIBLE);
         }

         else if (!mAudioFile.isEmpty())
         {
            view.setFocusable(false);
            view.setFocusableInTouchMode(false);
            // set the visibility of the view to visible
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new MyListener(mAudioFile));
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
         }
         v.setPressed(true);
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
      }
   }
}
