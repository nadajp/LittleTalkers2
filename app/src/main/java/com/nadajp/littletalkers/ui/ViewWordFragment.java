package com.nadajp.littletalkers.ui;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract.Words;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class ViewWordFragment extends ViewItemFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String DEBUG_TAG = "ViewWordFragment";
    private EditText mEditTranslation;
    private ContentResolver mResolver;
    private static final int WORD_LOADER = 10;

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

    public void updateExtraKidDetails() {
        //Log.i(DEBUG_TAG, "Updating extra kid details: " + mKidName);
        mTextHeading.setText(mKidName + " " + getString(R.string.said) + ":");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(WORD_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void startAudioRecording(boolean secondRecording) {
        Intent intent = new Intent(this.getActivity(), AudioRecordActivity.class);
        intent.putExtra(Prefs.TYPE, Prefs.TYPE_WORD);
        intent.putExtra(Prefs.TEMP_FILE_STEM, mTempFileStem);
        intent.putExtra(Prefs.SECOND_RECORDING, secondRecording);
        startActivityForResult(intent, RECORD_AUDIO_REQUEST);
    }

    public String getShareBody() {
        StringBuilder shareBody = new StringBuilder();
        shareBody.append("On ").append(mEditDate.getText()).append(", ")
                .append(mKidName).append(" said: ").append(mEditPhrase.getText());

        if (mEditTranslation.getText().toString()
                .compareTo(mEditPhrase.getText().toString()) != 0) {
            shareBody.append(", which means " + mEditTranslation.getText());
        }

        shareBody.append(".\n");

        return shareBody.toString();
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

        if (!update(phrase, msDate, location, translation, towhom, notes)) {
            if (!automatic) {
                mEditPhrase.requestFocus();
                mEditPhrase
                        .setError(getString(R.string.word_already_exists_error));
            }
            return -1;
        }
        // Word was updated successfully
        Toast.makeText(this.getActivity(), R.string.word_updated, Toast.LENGTH_SHORT).show();
        return mItemId;
    }

    private boolean update(String word, long date,
                               String location, String translation, String towhom,
                               String notes) {

        //Log.i(DEBUG_TAG, "Kid id for update: " + mCurrentKidId);
        //Log.i(DEBUG_TAG, "Word id for update: " + mItemId);

        ContentValues values = new ContentValues();
        values.put(Words.COLUMN_NAME_WORD, word);
        //Log.i(DEBUG_TAG, "Updating to current lanugage: " + mLanguage);
        values.put(Words.COLUMN_NAME_LANGUAGE, mLanguage);
        values.put(Words.COLUMN_NAME_DATE, date);
        values.put(Words.COLUMN_NAME_LOCATION, location);
        values.put(Words.COLUMN_NAME_AUDIO_FILE, mCurrentAudioFile);
        values.put(Words.COLUMN_NAME_TRANSLATION, translation);
        values.put(Words.COLUMN_NAME_TOWHOM, towhom);
        values.put(Words.COLUMN_NAME_NOTES, notes);

        // Updating Row with current item id
        mResolver.update(Words.CONTENT_URI,
                values,
                Words._ID + " = ?",
                new String[]{Long.valueOf(mItemId).toString()});
        return true;
    }

    public void clearExtraViews() {
        mEditTranslation.setText("");
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void displayWordHistory(View v) {
        Cursor cursor = getWordHistory();
        if (cursor.getCount() < 2)
            return;

        TextView title = (TextView) v.findViewById(R.id.txtWordHistory);
        title.setVisibility(View.VISIBLE);

        RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.detail_layout);

        int id = R.id.txtWordHistory;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                params.addRule(RelativeLayout.BELOW, id);
                int sideMargin = (int) (20 * getActivity().getResources()
                        .getDisplayMetrics().density);
                int bottomMargin = (int) (10 * getActivity().getResources()
                        .getDisplayMetrics().density);
                params.setMargins(sideMargin, 0, sideMargin, bottomMargin);
                ll.setLayoutParams(params);

                LayoutParams txtParams = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                TextView txtWord = new TextView(getActivity());
                TextView txtDate = new TextView(getActivity());

                txtWord.setLayoutParams(txtParams);
                txtWord.setTextAppearance(getActivity(),
                        R.style.DictionaryWordStyle);
                txtDate.setLayoutParams(txtParams);
                txtDate.setTextAppearance(getActivity(),
                        R.style.DictionaryDateStyle);

                txtWord.setText(cursor.getString(cursor
                        .getColumnIndex(Words.COLUMN_NAME_WORD)));

                long rawdate = cursor.getLong(cursor
                        .getColumnIndex(Words.COLUMN_NAME_DATE));
                txtDate
                        .setText(Utils.getDateForDisplay(rawdate, this.getActivity()));

                if (android.os.Build.VERSION.SDK_INT > 15) {
                    ll.setBackground(this.getResources().getDrawable(
                            R.drawable.white_card_background));
                } else {
                    ll.setBackgroundDrawable(this.getResources().getDrawable(
                            R.drawable.white_card_background));
                }

                ll.setPadding(20, 20, 20, 20);
                ll.addView(txtWord);
                ll.addView(txtDate);
                ll.setId(Utils.generateViewId());
                id = ll.getId();

                rl.addView(ll);

            } while (cursor.moveToNext());
            rl.setPadding(0, 0, 0, 30);
        }
        cursor.close();
    }

    private Cursor getWordHistory()
    {
        Cursor cursor = null;
        cursor = mResolver.query(Words.buildWordUri(mItemId),
                    new String[] { Words.COLUMN_NAME_TRANSLATION },
                    null,
                    null,
                    null);
        cursor.moveToFirst();
        String translation = cursor.getString(0);
        cursor.close();

        String[] projection = new String[]{
                Words._ID,
                Words.COLUMN_NAME_WORD,
                Words.COLUMN_NAME_DATE,
                Words.COLUMN_NAME_AUDIO_FILE
        };
        String selection = Words.COLUMN_NAME_TRANSLATION + " = ? AND " +
                Words.COLUMN_NAME_KID + " = ?";

        String[] selectionArgs = new String[] { translation, Integer.valueOf(mCurrentKidId).toString()};

        return mResolver.query(Words.CONTENT_URI, projection, selection, selectionArgs,
                               Words.COLUMN_NAME_DATE + " ASC");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
       if (id == WORD_LOADER){
           return new CursorLoader(getActivity(), Words.buildWordUri(mItemId), null, null, null, null);
       }
        else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            mEditPhrase.setText(cursor.getString(cursor
                    .getColumnIndex(Words.COLUMN_NAME_WORD)));
            // get date in milliseconds from db, convert to text, set current date
            long rawdate = cursor.getLong(cursor
                    .getColumnIndex(Words.COLUMN_NAME_DATE));
            mEditDate.setText(Utils.getDateForDisplay(rawdate, this.getActivity()));
            mDate.setTimeInMillis(rawdate);
            mEditLocation.setText(cursor.getString(
                    cursor.getColumnIndex(Words.COLUMN_NAME_LOCATION)));
            mEditTranslation.setText(cursor.getString(
                    cursor.getColumnIndex(Words.COLUMN_NAME_TRANSLATION)));
            mEditToWhom.setText(cursor.getString(
                    cursor.getColumnIndex(Words.COLUMN_NAME_TOWHOM)));
            mEditNotes.setText(cursor.getString(
                    cursor.getColumnIndex(Words.COLUMN_NAME_NOTES)));

            mLanguage = cursor.getString(cursor.getColumnIndex(Words.COLUMN_NAME_LANGUAGE));
            int position = mLanguageAdapter.getPosition(mLanguage);
            //Log.i(DEBUG_TAG, "Language: " + mLanguage + ", Pos: " + position);
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) mLangSpinner
                    .getAdapter();
            mLangSpinner.setSelection(adapter.getPosition(cursor.getString(cursor
                    .getColumnIndex(Words.COLUMN_NAME_LANGUAGE))));

            mCurrentAudioFile = cursor.getString(cursor
                    .getColumnIndex(Words.COLUMN_NAME_AUDIO_FILE));

            super.setAudio(getView());

            mTextHeading.setText(mKidName + " " + getString(R.string.said) + ":");
            //displayWordHistory(getView());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
