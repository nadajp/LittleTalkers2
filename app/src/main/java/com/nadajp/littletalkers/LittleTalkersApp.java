package com.nadajp.littletalkers;

import android.app.Application;

public class LittleTalkersApp extends Application
{

   @Override
   public void onCreate()
   {
      super.onCreate();

      // Pass in the app context. Different than an activity context.
      //DbSingleton.init(this);
   }
}
