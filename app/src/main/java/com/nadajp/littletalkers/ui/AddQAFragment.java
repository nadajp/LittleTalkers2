package com.nadajp.littletalkers.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;

public class AddQAFragment extends AddItemFragment {
    private static final String DEBUG_TAG = "AddQAFragment";
    private ContentResolver mResolver;
    private static final int INFO_DIALOG_ID = 1;

    // user interface elements
    private EditText mEditAnswer;
    private CheckBox mCheckAsked, mCheckAnswered;
    private TextView mTextHeadingQuestion;
    private TextView mTextHeadingAnswer;
    private ImageView mInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentLayout = R.layout.fragment_qa_detail;
        mEditPhraseResId = R.id.editQuestion;
        mTempFileStem = "tempQA";
        mResolver = getActivity().getContentResolver();

        View v = inflateFragment(R.layout.fragment_qa_detail, inflater, container, savedInstanceState);
        mEditAnswer = (EditText) v.findViewById(R.id.editAnswer);
        mCheckAsked = (CheckBox) v.findViewById(R.id.checkAsked);
        mCheckAnswered = (CheckBox) v.findViewById(R.id.checkAnswered);
        mTextHeadingQuestion = (TextView) v.findViewById(R.id.headingQuestion);
        mTextHeadingAnswer = (TextView) v.findViewById(R.id.headingAnswer);

        mInfo = (ImageView) v.findViewById(R.id.info);
        mInfo.setOnClickListener(this);

        super.insertData(v);

        return v;
    }

    public void updateExtraKidDetails() {
        mTextHeadingQuestion.setText(mKidName + " "
                + getString(R.string.asked_question) + "?");
        mTextHeadingAnswer.setText(mKidName + " "
                + getString(R.string.answered_question) + "?");
    }

    @Override
    public void setKidDefaults(Kid kid) {
        super.setKidDefaults(kid);
        updateExtraKidDetails();
    }

    public void startAudioRecording(boolean secondRecording) {
        Intent intent = new Intent(this.getActivity(), AudioRecordActivity.class);
        intent.putExtra(Prefs.TYPE, Prefs.TYPE_WORD);
        intent.putExtra(Prefs.TEMP_FILE_STEM, mTempFileStem);
        intent.putExtra(Prefs.SECOND_RECORDING, secondRecording);
        startActivityForResult(intent, RECORD_AUDIO_REQUEST);
    }

    public long savePhrase(boolean automatic) {
        if (mEditPhrase.length() == 0) {
            mEditPhrase.requestFocus();
            mEditPhrase.setError(getString(R.string.question_required_error));
            return -1;
        }

        if (mCheckAnswered.isChecked() && mEditAnswer.length() == 0) {
            mEditAnswer.requestFocus();
            mEditAnswer.setError(getString(R.string.answer_required_error));
            return -1;
        }

        saveAudioFile();

        // convert date to milliseconds for SQLite
        long msDate = mDate.getTimeInMillis();

        String question = mEditPhrase.getText().toString();
        String answer = mEditAnswer.getText().toString();
        String location = mEditLocation.getText().toString();
        String towhom = mEditToWhom.getText().toString();
        String notes = mEditNotes.getText().toString();
        int asked = mCheckAsked.isChecked() ? 1 : 0;
        int answered = mCheckAnswered.isChecked() ? 1 : 0;

        if (mItemId > 0) {  // already saved, updating...
            if (update(question, answer, asked, answered, msDate, towhom, location, notes) == false) {
                if (!automatic) {
                    mEditPhrase.requestFocus();
                    mEditPhrase
                            .setError(getString(R.string.QA_already_exists_error));
                }
                return -1;
            }
            // Item was updated successfully, show list
            Toast.makeText(this.getActivity(), R.string.question_updated, Toast.LENGTH_SHORT).show();

            return mItemId;
        }

        mItemId = insert(question, answer, asked, answered, msDate, towhom, location, notes);
        if (mItemId == -1) {
            if (!automatic) {
                mEditPhrase.requestFocus();
                mEditPhrase
                        .setError(getString(R.string.QA_already_exists_error));
            }
            return -1;
        }
        // QA was saved successful
        Toast.makeText(this.getActivity(), R.string.question_saved, Toast.LENGTH_SHORT).show();
        return mItemId;
    }

    private long insert(String question, String answer, int asked, int answered,
                        long date, String towhom, String location, String notes) {

        // check if qa already exists for this kid
        String argKidId = Integer.valueOf(mCurrentKidId).toString();
        Cursor cursor = mResolver.query(DbContract.Questions.CONTENT_URI,
                new String[]{DbContract.Questions._ID},
                DbContract.Questions.COLUMN_NAME_KID + " = ? AND " + DbContract.Questions.COLUMN_NAME_QUESTION + " = ? AND " +
                        DbContract.Questions.COLUMN_NAME_ANSWER + " = ?",
                new String[]{argKidId, question, answer},
                null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return -1;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(DbContract.Questions.COLUMN_NAME_KID, mCurrentKidId);
        values.put(DbContract.Questions.COLUMN_NAME_QUESTION, question);
        values.put(DbContract.Questions.COLUMN_NAME_ANSWER, answer);
        values.put(DbContract.Questions.COLUMN_NAME_ASKED, asked);
        values.put(DbContract.Questions.COLUMN_NAME_ANSWERED, answered);
        values.put(DbContract.Questions.COLUMN_NAME_LANGUAGE, mLanguage);
        values.put(DbContract.Questions.COLUMN_NAME_DATE, date);
        values.put(DbContract.Questions.COLUMN_NAME_LOCATION, location);
        values.put(DbContract.Questions.COLUMN_NAME_AUDIO_FILE, mCurrentAudioFile);
        values.put(DbContract.Questions.COLUMN_NAME_TOWHOM, towhom);
        values.put(DbContract.Questions.COLUMN_NAME_NOTES, notes);

        // Inserting Row
        Uri uri = mResolver.insert(DbContract.Questions.CONTENT_URI, values);
        return ContentUris.parseId(uri);
    }

    private boolean update(String question, String answer, int asked, int answered,
                           long date, String towhom, String location, String notes) {

        ContentValues values = new ContentValues();
        values.put(DbContract.Questions.COLUMN_NAME_QUESTION, question);
        values.put(DbContract.Questions.COLUMN_NAME_ANSWER, answer);
        values.put(DbContract.Questions.COLUMN_NAME_ASKED, asked);
        values.put(DbContract.Questions.COLUMN_NAME_ANSWERED, answered);
        values.put(DbContract.Questions.COLUMN_NAME_LANGUAGE, mLanguage);
        values.put(DbContract.Questions.COLUMN_NAME_DATE, date);
        values.put(DbContract.Questions.COLUMN_NAME_LOCATION, location);
        values.put(DbContract.Questions.COLUMN_NAME_AUDIO_FILE, mCurrentAudioFile);
        values.put(DbContract.Questions.COLUMN_NAME_TOWHOM, towhom);
        values.put(DbContract.Questions.COLUMN_NAME_NOTES, notes);

        // Inserting Row
        mResolver.update(DbContract.Questions.CONTENT_URI,
                values,
                DbContract.Questions._ID + " = ?",
                new String[]{Long.valueOf(mItemId).toString()});
        return true;
    }

    public void saveToPrefs() {
        // convert date to miliseconds for SQLite
        long msDate = mDate.getTimeInMillis();

        String question = mEditPhrase.getText().toString();
        String answer = mEditAnswer.getText().toString();
        String location = mEditLocation.getText().toString();
        String towhom = mEditToWhom.getText().toString();
        String notes = mEditNotes.getText().toString();
        String audioFile = mCurrentAudioFile;
        int asked = mCheckAsked.isChecked() ? 1 : 0;
        int answered = mCheckAnswered.isChecked() ? 1 : 0;

        Prefs.saveQAInfo(this.getActivity(), msDate, question, answer, location,
                towhom, notes, audioFile, asked, answered);
    }

    public void clearExtraViews() {
        mEditAnswer.setText("");
        mCheckAsked.setChecked(true);
        mCheckAnswered.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.info:
                InfoDialogFragment mInfoDialog = new InfoDialogFragment();
                mInfoDialog.setTargetFragment(this, INFO_DIALOG_ID);
                mInfoDialog.show(getFragmentManager(),
                        InfoDialogFragment.class.toString());
                break;
            default:
                return;
        }
    }

    public static class InfoDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setMessage(R.string.qa_info)
                    .setTitle(R.string.qa_info_title)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // dismiss
                                }
                            });
            builder.setIcon(R.drawable.ic_action_info);
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
