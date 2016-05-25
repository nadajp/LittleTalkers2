package com.nadajp.littletalkers.ui;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.contentprovider.PhraseSelection;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class KidProfileFragment extends Fragment {
    private static final String DEBUG_TAG = "KidProfileFragment";
    private int mKidId;
    private com.nadajp.littletalkers.model.Kid mKid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kid_profile,
                container, false);

        mKid = getArguments().getParcelable(getActivity().getString(R.string.kid_details));
        mKidId = mKid.getId();
        String pictureUri = mKid.getPictureUri();
        Bitmap profilePicture = null;

        if (pictureUri == null) {
            profilePicture = BitmapFactory.decodeResource(view.getResources(),
                    R.drawable.profile);
        } else {
            profilePicture = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureUri),
                    180, 180);
        }
        CircularImageView profile = (CircularImageView) view.findViewById(R.id.profile_picture);
        profile.setImageBitmap(profilePicture);

        ((TextView) view.findViewById(R.id.name)).setText(mKid.getName());
        TextView birthdate = (TextView) view.findViewById(R.id.tvBirthdate);

        birthdate.setText(Utils.getAge(mKid.getBirthdate()));

        TextView words = (TextView) view.findViewById(R.id.tvNumOfWords);
        TextView questions = (TextView) view.findViewById(R.id.tvNumOfQuestions);

        int nWords = new PhraseSelection().getNumberOfWords(getActivity().getContentResolver(), mKidId);
        String strWords = Integer.valueOf(nWords).toString()
                + " "
                + this.getString(R.string.words_and_phrases)
                + "!";

        int nQAs = new PhraseSelection().getNumberOfQAs(getActivity().getContentResolver(), mKidId);
        String strQA = Integer.valueOf(nQAs).toString()
                     + " "
                     + this.getString(R.string.questions_and_answers)
                     + "!";

        words.setText(strWords);
        questions.setText(strQA);

        ImageView edit = (ImageView) view.findViewById(R.id.icon_edit);
        edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddKidActivity.class);
                intent.putExtra(Prefs.CURRENT_KID_ID, mKidId);
                startActivity(intent);
            }

        });

        return view;
    }
}
