package com.nadajp.littletalkers.backup;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.nadajp.littletalkers.AppConstants;
import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;

/**
 * A fragment with a Google +1 button. Activities that contain this fragment
 * must implement the {@link SyncNowFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link SyncNowFragment#newInstance} factory method to create an instance of
 * this fragment.
 *
 */
public class SyncNowFragment extends Fragment
{
   // TODO: Rename parameter arguments, choose names that match
   // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   private static final String ARG_PARAM1 = "param1";
   private static final String ARG_PARAM2 = "param2";
   protected static final String DEBUG_TAG = "SyncNowFragment";
   
   private GoogleAccountCredential mCredential;
   public String mAccountName;

   // TODO: Rename and change types of parameters
   private String mParam1;
   private String mParam2;

   private Button  mButtonSyncNow;
   private ProgressBar mSpinner;
   
   private OnFragmentInteractionListener mListener;

   /**
    * Use this factory method to create a new instance of this fragment using
    * the provided parameters.
    *
    * @param param1
    *           Parameter 1.
    * @param param2
    *           Parameter 2.
    * @return A new instance of fragment SyncNowFragment.
    */
   // TODO: Rename and change types and number of parameters
   public static SyncNowFragment newInstance(String param1, String param2)
   {
      SyncNowFragment fragment = new SyncNowFragment();
      Bundle args = new Bundle();
      args.putString(ARG_PARAM1, param1);
      args.putString(ARG_PARAM2, param2);
      fragment.setArguments(args);
      return fragment;
   }

   public SyncNowFragment()
   {
      // Required empty public constructor
   }

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      if (getArguments() != null)
      {
         mParam1 = getArguments().getString(ARG_PARAM1);
         mParam2 = getArguments().getString(ARG_PARAM2);
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState)
   {
      // Inflate the layout for this fragment
      View view = inflater
            .inflate(R.layout.fragment_sync_now, container, false);

      mSpinner = (ProgressBar) view.findViewById(R.id.progress);

      mButtonSyncNow = (Button) view.findViewById(R.id.button_sync);
      final Long userId = Prefs.getUserId(this.getActivity());
      mCredential = GoogleAccountCredential
            .usingAudience(this.getActivity(), AppConstants.AUDIENCE);
      mCredential.setSelectedAccountName(Prefs.getAccountName(this.getActivity()));
      mButtonSyncNow.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            mSpinner.setVisibility(View.VISIBLE);
            if (userId != -1) {  // existing user, update
               Log.i(DEBUG_TAG, "Existing user, update!");
               new SyncToServer(mCredential, mSpinner, mButtonSyncNow).execute(SyncNowFragment.this.getActivity());           
           }
            else {  // new user, upload all data
               Log.i(DEBUG_TAG, "New user, upload all!");
               new UploadUserData(mCredential).execute(SyncNowFragment.this.getActivity());
            }            
         }        
      });           
      return view;
   }

   @Override
   public void onResume()
   {
      super.onResume();
   }

   // TODO: Rename method, update argument and hook method into UI event
   public void onButtonPressed(Uri uri)
   {
      if (mListener != null)
      {
         mListener.onFragmentInteraction(uri);
      }
   }

   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      try
      {
         mListener = (OnFragmentInteractionListener) activity;
      } catch (ClassCastException e)
      {
         throw new ClassCastException(activity.toString()
               + " must implement OnFragmentInteractionListener");
      }
   }

   @Override
   public void onDetach()
   {
      super.onDetach();
      mListener = null;
   }

   /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated to
    * the activity and potentially other fragments contained in that activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
   public interface OnFragmentInteractionListener
   {
      // TODO: Update argument type and name
      public void onFragmentInteraction(Uri uri);
   }
}
