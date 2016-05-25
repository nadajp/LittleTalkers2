package com.nadajp.littletalkers.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public abstract class ItemDetailFragment extends android.app.Fragment implements
        OnItemSelectedListener, OnClickListener, OnCompletionListener {
    public static final String ITEM_ID = "Item_ID";
    protected static final int RECORD_AUDIO_REQUEST = 4;
    final static Animation mAnimation = new AlphaAnimation(1, 0); // Change alpha
    private static final String DEBUG_TAG = "ItemDetailFragment";
    private static final int DELETE_AUDIO_DIALOG_ID = 1;
    private static final int REPLACE_AUDIO_DIALOG_ID = 2;

    protected int mCurrentKidId;
    protected long mItemId; // current item id, 0 if nothing has been saved yet
    protected String mCurrentAudioFile; // name of current audio file, empty
    protected String mTempFileStem; // either temp or tempQA
    protected Calendar mDate; // calendar for current date
    protected String mLanguage; // current language
    // from action bar
    protected String mKidName; // name of current kid, used for audio file name
    // common user interface elements
    protected EditText mEditPhrase, mEditDate, mEditLocation, mEditToWhom,
            mEditNotes;
    protected Spinner mLangSpinner;
    protected TextView mTextHeading;
    protected FloatingActionButton mButtonSave;
    // to be set by derived classes
    int mFragmentLayout; // res id of the layout for this fragment
    int mEditPhraseResId; // res id of the edittext containing main item (word,

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mDate.set(Calendar.YEAR, year);
            mDate.set(Calendar.MONTH, monthOfYear);
            mDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };
    ArrayAdapter<CharSequence> mLanguageAdapter;
    protected File mDirectory = null; // directory to store audio file
    protected File mOutFile = null; // audio file with permanent name
    protected File mTempFile = null; // temporary audio file (temp.3gp or tempQA.3gp)
    protected File mTempFile2 = null; // second temporary audio file, in case of
    protected MediaPlayer mPlayer; // audio player
    protected String mLocation = "Not set";
    protected RelativeLayout mRecordingLayout;
    protected Button mPlay;
    protected Button mDelete;
    protected ImageView mImgMic;
    protected EditText mEditMore;
    protected EditText mEditLess;
    protected boolean mAudioRecorded;

    public abstract long savePhrase(boolean automatic);

    public abstract void clearExtraViews();

    public abstract void startAudioRecording(boolean secondRecording);

    public abstract void updateExtraKidDetails();

    protected View inflateFragment(int resId, LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        View v = inflater.inflate(resId, container, false);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.detail_layout);

        /*
         *  Inflate shared layouts here
         */
        // Create language spinner
        mLangSpinner = (Spinner) layout.findViewById(R.id.spinner_language);
        mLangSpinner.setOnItemSelectedListener(this);
        mLanguageAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.array_languages, R.layout.lt_spinner_item);
        mLanguageAdapter.setDropDownViewResource(R.layout.lt_spinner_dropdown_item);
        mLangSpinner.setAdapter(mLanguageAdapter);

        mAnimation.setDuration(500); // duration - half a second
        // animation rate
        mAnimation.setInterpolator(new LinearInterpolator()); // do not alter
        // Repeat animation infinitely
        mAnimation.setRepeatCount(Animation.INFINITE);
        // Reverse animation at the
        // end so the button will fade back in
        mAnimation.setRepeatMode(Animation.REVERSE);

        mEditPhrase = (EditText) layout.findViewById(mEditPhraseResId);
        mEditDate = (EditText) layout.findViewById(R.id.editDate);
        mEditLocation = (EditText) layout.findViewById(R.id.editLocation);
        mEditToWhom = (EditText) layout.findViewById(R.id.editToWhom);
        mEditNotes = (EditText) layout.findViewById(R.id.editNotes);
        mTextHeading = (TextView) layout.findViewById(R.id.textHeading);
        mButtonSave = (FloatingActionButton) layout.findViewById(R.id.button_save);
        mRecordingLayout = (RelativeLayout) layout.findViewById(R.id.layout_recording);
        mImgMic = (ImageView) layout.findViewById(R.id.imgMic);
        mPlay = (Button) layout.findViewById(R.id.button_play);
        mDelete = (Button) layout.findViewById(R.id.button_delete);
        mEditMore = (EditText) layout.findViewById(R.id.edit_more);
        mEditLess = (EditText) layout.findViewById(R.id.edit_less);
        mDate = Calendar.getInstance();

        mEditDate.setOnClickListener(this);
        mImgMic.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);
        mEditMore.setOnClickListener(this);
        mEditLess.setOnClickListener(this);

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            // tablet, show all details
            showMoreFields(v, true);
        }

        // if audio has already been recorded, show play/delete buttons
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Prefs.AUDIO_RECORDED)) {
                mRecordingLayout.setVisibility(View.VISIBLE);
                mAudioRecorded = true;
                mCurrentAudioFile = savedInstanceState.getString(Prefs.AUDIO_FILE);
                TextView audioFile = (TextView) v.findViewById(R.id.text_recording);
                audioFile.setText(mCurrentAudioFile);
            }
            if (savedInstanceState.getBoolean(Prefs.SHOWING_MORE_FIELDS)) {
                showMoreFields(v, true);
            }
            mItemId = savedInstanceState.getLong(Prefs.ITEM_ID);
        } else {
            mItemId = getArguments().getLong(ITEM_ID);
        }
        Log.i(DEBUG_TAG, "item ID = " + mItemId);
        // the following listener is added in order to clear the error message
        // once the user starts typing
        mEditPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEditPhrase.setError(null);
            }
        });

        // set current date in the date field
        updateDate();

        // set directory for storing audio files
        String subdirectory = getString(R.string.app_name);
        mDirectory = Utils.getPublicDirectory(subdirectory, getActivity());

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editDate:
                showCalendar(v);
                break;
            case R.id.imgMic:
                startRecording();
                break;
            case R.id.button_play:
                clickedAudioPlay(v);
                break;
            case R.id.button_delete:
                deleteAudio();
                break;
            case R.id.button_save:
                saveItem(true);
                break;
            case R.id.edit_more:
                showMoreFields(getView(), true);
                break;
            case R.id.edit_less:
                showMoreFields(getView(), false);
                break;
            default:
                break;
        }
    }

    protected void showCalendar(View v) {
        new DatePickerDialog(v.getContext(), d, mDate.get(Calendar.YEAR),
                mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH)).show();
    }


    protected void updateDate() {
        mEditDate.setText(DateUtils.formatDateTime(getActivity(),
                mDate.getTimeInMillis(), DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_YEAR));
    }

    public void showMoreFields(View v, boolean more) {
        LinearLayout llMore = (LinearLayout) v.findViewById(R.id.layout_more);
        if (more) {
            llMore.setVisibility(View.VISIBLE);
            mEditMore.setVisibility(View.GONE);
            mEditLess.setVisibility(View.VISIBLE);
        } else {
            llMore.setVisibility(View.GONE);
            mEditMore.setVisibility(View.VISIBLE);
            mEditLess.setVisibility(View.GONE);
        }
    }

    public File getAudioFile() {
        return mOutFile;
    }

    private void clickedAudioPlay(View v) {
        if (mPlayer.isPlaying()) {
            stopPlaying();
        } else {
            startPlaying();
        }
    }

    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
    }

    private void startPlaying() {
        mPlay.setPressed(true);
        mPlay.startAnimation(mAnimation);

        try {
            if (mTempFile != null) {
                mPlayer.setDataSource(mTempFile.getAbsolutePath());
            } else {
                mPlayer.setDataSource(mOutFile.getAbsolutePath());
            }
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            //Log.e(DEBUG_TAG, "Audio player start failed: " + e.getMessage());
        }
    }

    private void stopPlaying() {
        if (mPlayer == null) {
            //Log.i(DEBUG_TAG, "Media Player is null in Stop()");
        }
        mPlayer.stop();
        mPlayer.reset();
        mPlay.clearAnimation();
        mPlay.setPressed(false);
        //Log.i(DEBUG_TAG, "Stopped.");
    }


    private void startRecording() {
        boolean secondRecording = false;

        if (mTempFile != null && mTempFile.exists()) {
            mTempFile2 = new File(mDirectory, mTempFileStem + "2.3gp");
            secondRecording = true;
        } else {
            mTempFile = new File(mDirectory, mTempFileStem + ".3gp");
        }
        startAudioRecording(secondRecording);
    }

    private void stopRecording() {
        mRecordingLayout.setVisibility(View.VISIBLE);
        TextView audioFile = (TextView) this.getView().findViewById(
                R.id.text_recording);

        // if a phrase has already been entered, save under real filename
        if (!(mEditPhrase.getText().toString().isEmpty())) {
            if (mOutFile != null || mTempFile2 != null) // if audio already exists, pop up dialog
            {
                ReplaceAudioDialogFragment dlg = new ReplaceAudioDialogFragment();
                Bundle args = new Bundle();
                args.putBoolean("phrase_entered", true);
                dlg.setArguments(args);
                dlg.setTargetFragment(this, REPLACE_AUDIO_DIALOG_ID);
                dlg.show(getFragmentManager(),
                        ReplaceAudioDialogFragment.class.toString());
            } else { // otherwise, just save audio
                saveItem(false);
            }
            return;
        }
        // otherwise, it has been saved in temp file, do not save item yet
        if (mAudioRecorded) {
            ReplaceAudioDialogFragment dlg = new ReplaceAudioDialogFragment();
            Bundle args = new Bundle();
            args.putBoolean("phrase_entered", false);
            dlg.setArguments(args);
            dlg.setTargetFragment(this, REPLACE_AUDIO_DIALOG_ID);
            dlg.show(getFragmentManager(),
                    ReplaceAudioDialogFragment.class.toString());
        } else {
            audioFile.setText(mTempFileStem + ".3gp");
        }
        mAudioRecorded = true;
        mCurrentAudioFile = mTempFile.getName();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RECORD_AUDIO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                stopRecording();
            }
        }
    }

    public void replaceTempFile(boolean replace) {
        if (replace) {
            mTempFile.delete();
            if (mTempFile2.renameTo(mTempFile)) {
                //Log.i(DEBUG_TAG, "Renamed temp files successfully.");
            }
            mTempFile2 = null;
        } else {
            mTempFile2.delete();
            mTempFile2 = null;
        }
    }

    private void deleteAudio() {
        DeleteAudioDialogFragment dlg = new DeleteAudioDialogFragment();
        dlg.setTargetFragment(this, DELETE_AUDIO_DIALOG_ID);
        dlg.show(getFragmentManager(), DeleteAudioDialogFragment.class.toString());
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mLanguage = parent.getItemAtPosition(pos).toString();
        Log.i(DEBUG_TAG, "Selected language: " + mLanguage);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void confirmDeleteAudio() {
        mAudioRecorded = false;
        mCurrentAudioFile = "";
        if (mOutFile != null && mOutFile.exists()) {
            mOutFile.delete();
            mOutFile = null;

            if (mEditPhrase.getText().length() > 0) {
                saveItem(false);
            }
        }
        if (mTempFile != null && mTempFile.exists()) {
            mTempFile.delete();
            mTempFile = null;
        }
        mRecordingLayout.setVisibility(View.GONE);
    }

    protected void saveItem(boolean exit) {
        mItemId = savePhrase(!exit);
        if (mOutFile != null && mOutFile.exists()) {
            mCurrentAudioFile = mOutFile.getAbsolutePath();
        }
    }

    protected void saveAudioFile() {
        if (mTempFile != null && mTempFile.exists()) {
            saveFile(false);
        } else if (mOutFile != null && mOutFile.exists()) {
            renameFile();
        }
        if (mCurrentAudioFile != null) {
            String[] a = mCurrentAudioFile.split("/");
            String filename = a[a.length - 1];
            TextView audioFile = (TextView) this.getView().findViewById(
                    R.id.text_recording);
            audioFile.setText(filename);
        }
    }

    public void clearForm() {
        mEditPhrase.setText("");
        mDate = Calendar.getInstance();
        updateDate();
        mEditToWhom.setText("");
        mEditNotes.setText("");
        mRecordingLayout.setVisibility(View.GONE);
        mAudioRecorded = false;
        mEditPhrase.setError(null);
        clearExtraViews();
    }

    protected void setAudio(View v) {
        //Log.i(DEBUG_TAG, "Audio File: " + mCurrentAudioFile);
        if (mCurrentAudioFile != null && !mCurrentAudioFile.isEmpty()) {
            mOutFile = new File(mCurrentAudioFile);
            mRecordingLayout.setVisibility(View.VISIBLE);
            mAudioRecorded = true;

            String[] a = mCurrentAudioFile.split("/");
            String filename = a[a.length - 1];
            TextView audioFile = (TextView) v.findViewById(R.id.text_recording);
            audioFile.setText(filename);
        }
    }

    private String getFilename() {
        String[] a = mEditPhrase.getText().toString().split(" ");
        StringBuffer str = new StringBuffer(a[0].trim());
        for (int i = 1; i < a.length; i++) {
            str.append(a[i].trim());
            if (i == 5) {
                break;
            }
        }
        return mKidName + "-" + str + mDate.getTimeInMillis() + ".3gp";
    }

    private void renameFile() {
        String filename = getFilename();
        File newfile = new File(mDirectory, filename);

        if (mOutFile.getAbsolutePath().equals(newfile.getAbsolutePath())) {
            return;
        }
        if (newfile.exists()) {
            newfile.delete();
        }

        //Log.i(DEBUG_TAG, "Oldfile: " + mOutFile.getAbsolutePath());
        //Log.i(DEBUG_TAG, "Newfile: " + newfile.getAbsolutePath());

        mOutFile.renameTo(newfile);

      /*
      if (mOutFile.renameTo(newfile))
      {
         Log.i(DEBUG_TAG, "Rename succesful");
      } else
      {
         Log.i(DEBUG_TAG, "Rename failed");
      }*/

        mOutFile.delete();
        mOutFile = newfile;
        mTempFile = null;
        mCurrentAudioFile = mOutFile.getAbsolutePath();
    }

    protected void saveFile(boolean replace) {
        mOutFile = new File(mDirectory, getFilename());

        if (mOutFile.exists()) {
            mOutFile.delete();
        }

        if (replace) {
            mTempFile2.renameTo(mOutFile);
            mTempFile2.delete();
            mTempFile2 = null;
        } else {
            mTempFile.renameTo(mOutFile);
        }
      /*
      if (mTempFile.renameTo(mOutFile))
      {
         Log.i(DEBUG_TAG, "Rename succesful");
      } else
      {
         Log.i(DEBUG_TAG, "Rename failed");
      }*/

        mTempFile.delete();
        mTempFile = null;
        String[] paths = {mOutFile.getAbsolutePath()};
        String[] mimes = {"audio/*"};
        MediaScannerConnection.scanFile(getActivity(), paths, mimes,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        //Log.i("ExternalStorage", "Scanned " + path + ":");
                        //Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
        mCurrentAudioFile = mOutFile.getAbsolutePath();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Prefs.AUDIO_RECORDED, mAudioRecorded);
        outState.putLong(Prefs.ITEM_ID, mItemId);
        if (mRecordingLayout.getVisibility() == View.VISIBLE) {
            outState.putString(Prefs.AUDIO_FILE, mCurrentAudioFile);
            //Log.i(DEBUG_TAG, "AUDIO RECORDED.");
        }
        if (mEditLess.getVisibility() == View.VISIBLE) {
            outState.putBoolean(Prefs.SHOWING_MORE_FIELDS, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onPause() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDirectory = null;
        mOutFile = null;
        mDate = null;
        mTempFile = null;
    }

    public static class ReplaceAudioDialogFragment extends DialogFragment {
        private boolean mPhraseEntered; // has the phrase already been entered for
        // this audio?

        public ReplaceAudioDialogFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mPhraseEntered = getArguments().getBoolean("phrase_entered");
            if (savedInstanceState != null) {
                mPhraseEntered = savedInstanceState
                        .getBoolean(Prefs.PHRASE_ENTERED);
            }
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setMessage(R.string.replace_audio_dialog)
                    .setPositiveButton(R.string.replace_audio,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (mPhraseEntered) {
                                        if (((ItemDetailFragment) getTargetFragment()).mItemId > 0) {
                                            ((ItemDetailFragment) getTargetFragment())
                                                    .saveItem(false);
                                        } else {
                                            ((ItemDetailFragment) getTargetFragment()).saveFile(true);
                                            ((ItemDetailFragment) getTargetFragment())
                                                    .saveItem(false);

                                        }
                                    } else {
                                        ((ItemDetailFragment) getTargetFragment())
                                                .replaceTempFile(true);
                                    }

                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (mPhraseEntered) {
                                        ((ItemDetailFragment) getTargetFragment()).mTempFile = null;
                                    } else {
                                        ((ItemDetailFragment) getTargetFragment())
                                                .replaceTempFile(false);
                                    }
                                }
                            });

            // Create the AlertDialog object and return it
            return builder.create();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putBoolean(Prefs.PHRASE_ENTERED, mPhraseEntered);
            super.onSaveInstanceState(outState);
        }
    }

    public static class DeleteAudioDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setMessage(R.string.delete_audio_dialog)
                    .setPositiveButton(R.string.delete,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ((ItemDetailFragment) getTargetFragment())
                                            .confirmDeleteAudio();
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
