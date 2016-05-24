package com.nadajp.littletalkers.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class ViewQAFragment extends ViewItemFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String DEBUG_TAG = "ViewQAFragment";
    private ContentResolver mResolver;
    private static final int QA_LOADER = 10;
    private static final int INFO_DIALOG_ID = 3;

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
    public void onActivityCreated(Bundle savedInstanceState)
    {
        getLoaderManager().initLoader(QA_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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

    public String getShareBody() {
        StringBuilder shareBody = new StringBuilder();

        shareBody.append("On ").append(mEditDate.getText()).append(", ");

        if (mEditPhrase.length() > 0){
            if (mCheckAsked.isChecked()) {
                // Kid asked the question
                shareBody.append(mKidName).append(" asked: ");
            }
            else if (mEditToWhom.length() > 0) {
                shareBody.append(mEditToWhom.getText().append(" asked: "));
            }
            else {
                shareBody.append(mKidName).append(" was asked: ");
            }
            shareBody.append(mEditPhrase.getText()).append("? ");
        }
        if (mEditAnswer.length() > 0) {
            if (mCheckAnswered.isChecked()) {
                shareBody.append(mKidName).append(" answered: ").append(mEditAnswer.getText());
            } else if (mEditToWhom.length() > 0) {
                shareBody.append(mEditToWhom.getText()).append(" answered: ").append(mEditAnswer.getText());
            }
            else {
                shareBody.append("(The answer he was given was: ").append(mEditAnswer.getText()).append(")");
            }
        }
        shareBody.append(".\n");

        return shareBody.toString();
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

        if (update(question, answer, asked, answered, msDate, towhom, location, notes) == false) {
            if (!automatic) {
                mEditPhrase.requestFocus();
                mEditPhrase
                        .setError(getString(R.string.QA_already_exists_error));
            }
            return -1;
        }
        // Word was updated successfully, show dictionary
        Toast toast = Toast.makeText(this.getActivity(),
                R.string.question_updated, Toast.LENGTH_SHORT);
        toast.show();
        // invalidate menu to add sharing capabilities
        this.getActivity().invalidateOptionsMenu();

        return mItemId;
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
       if (id == QA_LOADER){
           return new CursorLoader(getActivity(), DbContract.Questions.buildQuestionsUri(mItemId), null, null, null, null);
       }
        else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            Log.i(DEBUG_TAG, "Inserting qa details");
            cursor.moveToFirst();
            mEditPhrase.setText(cursor.getString(cursor
                    .getColumnIndex(DbContract.Questions.COLUMN_NAME_QUESTION)));
            mEditAnswer.setText(cursor.getString(cursor
                    .getColumnIndex(DbContract.Questions.COLUMN_NAME_ANSWER)));

            int asked = cursor.getInt(cursor
                    .getColumnIndex(DbContract.Questions.COLUMN_NAME_ASKED));
            if (asked == 0) {
                mCheckAsked.setChecked(false);
            } else
                mCheckAsked.setChecked(true);

            int answered = cursor.getInt(cursor
                    .getColumnIndex(DbContract.Questions.COLUMN_NAME_ANSWERED));
            if (answered == 0) {
                mCheckAnswered.setChecked(false);
            } else
                mCheckAnswered.setChecked(true);

            // get date in miliseconds from db, convert to text, set current date
            long rawdate = cursor.getLong(cursor
                    .getColumnIndex(DbContract.Words.COLUMN_NAME_DATE));
            mEditDate.setText(Utils.getDateForDisplay(rawdate, this.getActivity()));
            mDate.setTimeInMillis(rawdate);

            // mEditDate.setText(cursor.getString(cursor.getColumnIndex(DbContract.Words.COLUMN_NAME_DATE)).toString());
            mEditLocation.setText(cursor.getString(
                    cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_LOCATION))
                    .toString());
            mEditToWhom.setText(cursor.getString(
                    cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_TOWHOM))
                    .toString());
            mEditNotes.setText(cursor.getString(
                    cursor.getColumnIndex(DbContract.Questions.COLUMN_NAME_NOTES))
                    .toString());

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) mLangSpinner
                    .getAdapter();
            mLangSpinner.setSelection(adapter.getPosition(cursor.getString(cursor
                    .getColumnIndex(DbContract.Questions.COLUMN_NAME_LANGUAGE))));

            mCurrentAudioFile = cursor.getString(cursor
                    .getColumnIndex(DbContract.Questions.COLUMN_NAME_AUDIO_FILE));

            if (asked == 1) {
                mTextHeadingQuestion.setText(mKidName + " "
                        + getString(R.string.asked) + ":");
            } else {
                mTextHeadingQuestion.setText(getString(R.string.question) + ":");
            }
            if (answered == 1) {
                mTextHeadingAnswer.setText(mKidName + " "
                        + getString(R.string.answered) + ":");
            } else {
                mTextHeadingAnswer.setText(getString(R.string.answer) + ":");
            }
            cursor.close();
            setAudio(getView());
        }
        else {
            Log.i(DEBUG_TAG, "No qa details to insert");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
