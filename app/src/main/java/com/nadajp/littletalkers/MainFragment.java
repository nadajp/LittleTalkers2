package com.nadajp.littletalkers;

import android.app.Activity;
import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainFragment extends Fragment implements OnClickListener
{   
   private AddKidListener mListener;
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      View view = inflater.inflate(R.layout.fragment_main,
            container, false); 
      ImageView img1 = (ImageView) view.findViewById(R.id.red_button);
      ImageView img2 = (ImageView) view.findViewById(R.id.logo);
            
      img1.setOnClickListener(this);
      img2.setOnClickListener(this);

      return view;
   }     
   

   @Override
   public void onClick(View v)
   {
      mListener.clickedAddKid();    
   }
   
   public interface AddKidListener
   {
      public void clickedAddKid();
   }
   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      if (activity instanceof AddKidListener)
      {
         mListener = (AddKidListener) activity;
      } else
      {
         throw new ClassCastException(activity.toString()
               + " must implemenet MainFragment.AddKidListener");
      }
   }

   @Override
   public void onDetach()
   {
      super.onDetach();
      mListener = null;
   }
}
