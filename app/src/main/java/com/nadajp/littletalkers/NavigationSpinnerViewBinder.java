package com.nadajp.littletalkers;

import com.nadajp.littletalkers.database.DbContract;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.ImageView;

public class NavigationSpinnerViewBinder implements ViewBinder
{

   @Override
   public boolean setViewValue(View view, Cursor cursor, int columnIndex)
   {
      if (columnIndex == cursor
            .getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI))
      {
         String pictureUri = cursor.getString(cursor
               .getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));
         Bitmap profilePicture = null;
         if (pictureUri == null)
         {
            profilePicture = BitmapFactory.decodeResource(view.getResources(),
                  R.drawable.profile);
         } else
         {
            profilePicture = BitmapFactory.decodeFile(pictureUri);
         }
         ((ImageView) view).setImageBitmap(profilePicture);
         return true;
      }

      return false;
   }

}
