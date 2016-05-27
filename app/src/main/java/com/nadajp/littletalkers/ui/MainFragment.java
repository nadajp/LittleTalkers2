package com.nadajp.littletalkers.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nadajp.littletalkers.R;

public class MainFragment extends Fragment implements OnClickListener {
    private AddKidListener mListener;
    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,
                container, false);
        ImageView img1 = (ImageView) view.findViewById(R.id.red_button);
        ImageView img2 = (ImageView) view.findViewById(R.id.logo);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;
    }

    @Override
    public void onClick(View v) {
        mListener.clickedAddKid();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof AddKidListener) {
            mListener = (AddKidListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implemenet MainFragment.AddKidListener");
        }
        super.onAttach(context);
    }

    // for API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof AddKidListener) {
            mListener = (AddKidListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet MainFragment.AddKidListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AddKidListener {
        void clickedAddKid();
    }
}
