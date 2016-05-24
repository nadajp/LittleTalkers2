package com.nadajp.littletalkers.ui;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract.Words;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;

public class AddWordFragment extends AddItemFragment {
    private static final String DEBUG_TAG = "AddWordFragment";
    private EditText mEditTranslation;
    private ContentResolver mResolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentLayout = R.layout.fragment_word_detail;
        mEditPhraseResId = R.id.editWord;
        mTempFileStem = "temp";
        mResolver = getActivity().getContentResolver();
        View v = inflateFragment(R.layout.fragment_word_detail, inflater, container, savedInstanceState);
        mEditTranslation = (EditText) v.findViewById(R.id.editTranslation);

        super.insertData(v);
        return v;
    }

    @Override
    public void setKidDefaults(Kid kid){
        super.setKidDefaults(kid);
        mTextHeading.setText(mKidName + " "
                + getString(R.string.said_something) + " ?");
    }

    public void updateExtraKidDetails() {
        Log.i(DEBUG_TAG, "Updating extra kid details: " + mKidName);
        mTextHeading.setText(mKidName + " "
                    + getString(R.string.said_something) + " ?");
    }


    public void initializeExtras(View v) {
        //mEditTranslation = (EditText) v.findViewById(R.id.editTranslation);
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
            mEditPhrase.setError(getString(R.string.word_required_error));
            return -1;
        }

        super.saveAudioFile();

        // convert date to miliseconds for SQLite
        long msDate = mDate.getTimeInMillis();

        String phrase = mEditPhrase.getText().toString();
        String location = mEditLocation.getText().toString();
        String translation = mEditTranslation.length() == 0 ? phrase
                : mEditTranslation.getText().toString();
        String towhom = mEditToWhom.getText().toString();
        String notes = mEditNotes.getText().toString();

        mItemId = insert(phrase, msDate, location, translation, towhom, notes);

        if (mItemId == -1) {
            if (!automatic) {
                mEditPhrase.requestFocus();
                mEditPhrase
                        .setError(getString(R.string.word_already_exists_error));
            }
            return -1;
        }

        // word was saved successfully
        Toast toast = Toast.makeText(this.getActivity(), R.string.word_saved,
                Toast.LENGTH_SHORT);
        toast.show();
        return mItemId;
    }

    private long insert(String word, long date, String location, String translation,
                        String towhom, String notes) {
        // check if word already exists for this kid
        String argKidId = Integer.valueOf(mCurrentKidId).toString();
        Log.i(DEBUG_TAG, "Kid id at insert: " + argKidId);
        Cursor cursor = mResolver.query(Words.CONTENT_URI,
                new String[]{Words._ID},
                Words.COLUMN_NAME_KID + " = ? AND " + Words.COLUMN_NAME_WORD + " = ?",
                new String[]{argKidId, word},
                null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return -1;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(Words.COLUMN_NAME_KID, mCurrentKidId);
        values.put(Words.COLUMN_NAME_WORD, word);
        values.put(Words.COLUMN_NAME_LANGUAGE, mLanguage);
        values.put(Words.COLUMN_NAME_DATE, date);
        values.put(Words.COLUMN_NAME_LOCATION, location);
        values.put(Words.COLUMN_NAME_AUDIO_FILE, mCurrentAudioFile);
        values.put(Words.COLUMN_NAME_TRANSLATION, translation);
        values.put(Words.COLUMN_NAME_TOWHOM, towhom);
        values.put(Words.COLUMN_NAME_NOTES, notes);

        // Inserting Row
        Uri uri = mResolver.insert(Words.CONTENT_URI, values);
        return ContentUris.parseId(uri);
    }

    public void saveToPrefs() {
        // convert date to miliseconds for SQLite
        long msDate = mDate.getTimeInMillis();

        String phrase = mEditPhrase.getText().toString();

        String location = mEditLocation.getText().toString();
        String translation = mEditTranslation.length() == 0 ? phrase
                : mEditTranslation.getText().toString();
        String towhom = mEditToWhom.getText().toString();
        String notes = mEditNotes.getText().toString();

        String audioFile = mCurrentAudioFile;

        Prefs.savePhraseInfo(this.getActivity(), msDate, phrase, location,
                translation, towhom, notes, audioFile);
    }

    public void clearExtraViews() {
        mEditTranslation.setText("");
    }
}
