package com.nadajp.littletalkers.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbContract.Kids;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

public class AddKidFragment extends Fragment implements OnClickListener,
        OnItemSelectedListener {
    private static final String DEBUG_TAG = "AddKidFragment";
    private static final String TEMP_PHOTO_FILE_NAME = "temp_profile.png";
    private static final int TAKE_PICTURE = 0;
    private static final int PICK_FROM_FILE = 1;
    private static final int CROP_PICTURE = 2;
    private static final int IMAGE_SIZE = 180;
    Calendar mBirthDate = Calendar.getInstance();
    private int mKidId;
    private OnKidAddedListener mListener;
    private long mBirthDateMillis;
    private EditText mEditName;
    private EditText mEdiBirthDate;
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mBirthDate.set(Calendar.YEAR, year);
            mBirthDate.set(Calendar.MONTH, monthOfYear);
            mBirthDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };
    private EditText mEditLocation;
    private CircularImageView mImgProfilePic;
    private FloatingActionButton mButtonSave;
    private Spinner mSpinnerLanguage;
    private Uri mUriPicture;
    private String mPicturePath;
    private String mLanguage;
    private boolean mTempBitmapSaved;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_kid, container, false);

        mEditName = (EditText) v.findViewById(R.id.edit_name);
        mEdiBirthDate = (EditText) v.findViewById(R.id.edit_birthdate);
        mEditLocation = (EditText) v.findViewById(R.id.edit_default_location);
        mImgProfilePic = (CircularImageView) v.findViewById(R.id.image_profile);
        mButtonSave = (FloatingActionButton) v.findViewById(R.id.button_save);

        mEdiBirthDate.setOnClickListener(this);
        mImgProfilePic.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);

        // Create a spinner for language selection
        mSpinnerLanguage = (Spinner) v.findViewById(R.id.spinner_language);
        mSpinnerLanguage.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.array_languages, R.layout.lt_spinner_item);
        adapter.setDropDownViewResource(R.layout.lt_spinner_dropdown_item);
        mSpinnerLanguage.setAdapter(adapter);

        mSpinnerLanguage.setSelection(adapter
                .getPosition(getString(R.string.app_language)));

        if (getActivity().getIntent().hasExtra(Prefs.CURRENT_KID_ID)) {
            mKidId = getActivity().getIntent().getIntExtra(Prefs.CURRENT_KID_ID, -1);
        } else if (getArguments() != null) {
            mKidId = getArguments().getInt(Prefs.CURRENT_KID_ID);
        } else {
            mKidId = -1;
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.getString(Prefs.PROFILE_PIC_PATH) != null) {
                mUriPicture = Uri.parse(savedInstanceState
                        .getString(Prefs.PROFILE_PIC_PATH));
                try {
                    Bitmap photo = ThumbnailUtils.extractThumbnail(
                            BitmapFactory.decodeFile(mPicturePath), IMAGE_SIZE,
                            IMAGE_SIZE);
                    mImgProfilePic.setImageBitmap(photo);

                    photo = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If editing/viewing an existing kid, fill all the fields
        if (mKidId > 0) {
            insertKidDetails(mKidId);
        } else {
            Bitmap profilePicture = ThumbnailUtils.extractThumbnail(BitmapFactory
                            .decodeResource(v.getResources(), R.drawable.add_profile),
                    IMAGE_SIZE, IMAGE_SIZE);
            mImgProfilePic.setImageBitmap(profilePicture);
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_birthdate:
                showCalendar(v);
                break;
            case R.id.image_profile:
                showProfileDialog();
                break;
            case R.id.button_save:
                saveKid();
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mLanguage = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void updateDate() {
        mBirthDateMillis = mBirthDate.getTimeInMillis();
        mEdiBirthDate.setText(DateUtils.formatDateTime(this.getActivity(),
                mBirthDateMillis, DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_YEAR));
    }

    private void showCalendar(View v) {
        new DatePickerDialog(v.getContext(), d, mBirthDate.get(Calendar.YEAR),
                mBirthDate.get(Calendar.MONTH),
                mBirthDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void insertKidDetails(int kidId) {
        Cursor cursor = getActivity().getContentResolver().
                query(Kids.buildKidsUri(kidId), null, null, null, null);
        cursor.moveToFirst();

        mEditName
                .setText(cursor.getString(
                        cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME))
                        .toString());
        mBirthDateMillis = cursor.getLong(cursor
                .getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS));
        mBirthDate.setTimeInMillis(mBirthDateMillis);
        mEdiBirthDate.setText(Utils.getDateForDisplay(mBirthDateMillis,
                this.getActivity()));
        mEditLocation
                .setText(cursor
                        .getString(
                                cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION))
                        .toString());

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) mSpinnerLanguage
                .getAdapter();
        mSpinnerLanguage.setSelection(adapter.getPosition(cursor.getString(cursor
                .getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE))));

        mPicturePath = cursor.getString(cursor
                .getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));
        cursor.close();

        Bitmap profilePicture = null;

        if (mPicturePath == null) {
            profilePicture = BitmapFactory.decodeResource(getResources(),
                    R.drawable.add_profile);
        } else {
            profilePicture = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(mPicturePath), IMAGE_SIZE, IMAGE_SIZE);
        }

        mImgProfilePic.setImageBitmap(profilePicture);
    }

    private void saveKid() {
        String name, location;

        // Name and Birthday are required, do validation
        if (mEditName.length() == 0) {
            mEditName.setError(getString(R.string.name_required_error));
            return;
        }

        if (mEdiBirthDate.length() == 0) {
            mEdiBirthDate.requestFocus();
            mEdiBirthDate.setError(getString(R.string.birthdate_required_error));
            return;
        }

        name = mEditName.getText().toString();
        location = mEditLocation.getText().toString();

        if (this.mTempBitmapSaved) {
            renameFile();
        }

        // Adding new kid
        if (mKidId < 0) {
            mKidId = insertKid(name, location);
        }

        // Updating a current kid
        else {
            if (!updateKid(name, location)) {
                // TODO error message (duplicate kid name)
                return;
            } else {
                // Kid was updated
                String msg = name + " " + getString(R.string.kid_updated);
                Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
                toast.show();
                mListener.onKidUpdated(mKidId);
                return;
            }
        }

        if (mKidId == -1) {
            mEditName.setError(getString(R.string.kid_already_exists_error));
            return;
        }

        // Kid was saved
        String msg = name + " " + getString(R.string.kid_saved);
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        getActivity().invalidateOptionsMenu();
        Prefs.saveKidId(getActivity(), mKidId);
        mListener.onKidAdded(mKidId);
    }

    private int insertKid(String name, String location) {
        ContentResolver resolver = this.getActivity().getContentResolver();
        // check if name already exists
        Cursor cursor = resolver.
                query(Kids.CONTENT_URI,
                        new String[]{Kids._ID},
                        Kids.COLUMN_NAME_NAME + " = ? ",
                        new String[]{name},
                        null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return -1;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(Kids.COLUMN_NAME_NAME, name);
        values.put(Kids.COLUMN_NAME_DEFAULT_LOCATION, location);
        values.put(Kids.COLUMN_NAME_DEFAULT_LANGUAGE, mLanguage);
        values.put(Kids.COLUMN_NAME_PICTURE_URI, mPicturePath);
        values.put(Kids.COLUMN_NAME_BIRTHDATE_MILLIS, mBirthDateMillis);

        // Insert row and return row id
        Uri uri = resolver.insert(Kids.CONTENT_URI, values);
        return (int) ContentUris.parseId(uri);
    }

    private boolean updateKid(String name, String location) {
        // check if another kid with this name but different ID already exists
        ContentResolver resolver = getActivity().getContentResolver();
        // check if name already exists
        String idArg = Integer.valueOf(mKidId).toString();
        Cursor cursor = resolver.
                query(Kids.CONTENT_URI,
                        new String[]{Kids.COLUMN_NAME_NAME},
                        Kids.COLUMN_NAME_NAME + " = ? AND " + Kids._ID + " != ? ",
                        new String[]{name, idArg},
                        null);
        if (cursor.getCount() > 0) {
            Log.i(DEBUG_TAG, "Kid with same name but different ID already exists");
            cursor.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(Kids.COLUMN_NAME_NAME, name);
        values.put(Kids.COLUMN_NAME_DEFAULT_LANGUAGE, mLanguage);
        values.put(Kids.COLUMN_NAME_DEFAULT_LOCATION, location);
        values.put(Kids.COLUMN_NAME_PICTURE_URI, mPicturePath);
        values.put(Kids.COLUMN_NAME_BIRTHDATE_MILLIS, mBirthDateMillis);

        // update
        resolver.update(Kids.CONTENT_URI, values,
                Kids._ID + " = ? ",
                new String[]{idArg});
        return true;
    }

    public void showProfileDialog() {
        chooseProfilePic();
    }

    private void chooseProfilePic() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        PackageManager manager = getActivity().getPackageManager();
        List<ResolveInfo> info = manager.queryIntentActivities(intent, 0);
        if (info.size() > 0) {
            startActivityForResult(intent, PICK_FROM_FILE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        mUriPicture = data.getData();
        switch (requestCode) {
            case TAKE_PICTURE:
                // mBitmapProfile = BitmapFactory.decodeFile(mUriPicture.getPath());
                // Bitmap thumb = Bitmap.createScaledBitmap(mBitmapProfile, IMAGE_SIZE,
                // IMAGE_SIZE, false);
                // mButtonProfilePic.setImageBitmap(thumb);
                break;

            case PICK_FROM_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    mUriPicture = data.getData();
                    try {
                        cropPicture(mUriPicture); //the user's device may not support cropping
                    } catch (ActivityNotFoundException aNFE) {
                        //display an error message if user device doesn't support
                        String errorMessage = "Sorry - your device doesn't support the crop action!";
                        Toast toast = Toast.makeText(this.getActivity(), errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                        try {
                            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
                                    MediaStore.Images.Media.getBitmap(this.getActivity()
                                            .getContentResolver(), mUriPicture), IMAGE_SIZE,
                                    IMAGE_SIZE);

                            mImgProfilePic.setImageBitmap(thumbnail);
                            saveProfileBitmapFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }

            case CROP_PICTURE:
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                mUriPicture = data.getData();
                mImgProfilePic.setImageBitmap(thePic);
                saveProfileBitmapFile();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void cropPicture(Uri picUri) {
        // call the standard crop action intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        // indicate image type and Uri of image
        cropIntent.setDataAndType(picUri, "image/*");
        // set crop properties
        cropIntent.putExtra("crop", "true");
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // indicate output X and Y
        cropIntent.putExtra("outputX", 120);
        cropIntent.putExtra("outputY", 120);
        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        // start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, CROP_PICTURE);
    }

    public Bitmap decodeUri(Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(this.getActivity().getContentResolver()
                .openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(this.getActivity().getContentResolver()
                .openInputStream(uri), null, o2);
    }

    private void saveProfileBitmapFile() {
        if (mUriPicture == null) {
            mPicturePath = "";
            return;
        }
        Bitmap photo = null;
        try {
            photo = decodeUri(mUriPicture, IMAGE_SIZE);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }

        String filename;
        boolean temp = mEditName.getText().toString().isEmpty();
        if (temp) {
            filename = TEMP_PHOTO_FILE_NAME;
        } else {
            filename = mEditName.getText().toString() + ".png";
        }

        File file = null;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            file = new File(getActivity().getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES), filename);
        } else {
            file = new File(getActivity().getFilesDir(), filename);
        }

        mPicturePath = file.getAbsolutePath();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
                if (temp) {
                    mTempBitmapSaved = true;
                }
            } catch (Throwable ignore) {

            }
        }
    }

    public void renameFile() {
        File oldfile = new File(mPicturePath);
        File newfile = null;
        String filename = mEditName.getText().toString() + ".png";

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            newfile = new File(getActivity().getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES), filename);
        } else {
            newfile = new File(getActivity().getFilesDir(), filename);
        }

        mPicturePath = newfile.getAbsolutePath();
        if (newfile.exists()) {
            newfile.delete();
        }

        oldfile.renameTo(newfile);
        oldfile.delete();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnKidAddedListener) {
            mListener = (OnKidAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implemenet AddKidFragment.OnKidAddedListener");
        }
        super.onAttach(context);
    }

    // for API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnKidAddedListener) {
            mListener = (OnKidAddedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet AddKidFragment.OnKidAddedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPicturePath != null) {
            outState.putString(Prefs.PROFILE_PIC_PATH, mPicturePath);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface OnKidAddedListener {
        void onKidAdded(int kidId);
        void onKidUpdated(int kidId);
    }
}
