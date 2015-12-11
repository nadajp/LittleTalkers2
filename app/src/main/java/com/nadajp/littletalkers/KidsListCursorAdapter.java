package com.nadajp.littletalkers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nadajp.littletalkers.contentprovider.PhraseSelection;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.utils.Prefs;
import com.nadajp.littletalkers.utils.Utils;

public class KidsListCursorAdapter extends CursorAdapter {
    protected static final String DEBUG_TAG = "KidsListCursorAdapter";
    private static final int THUMBNAIL_SIZE = 100;
    public ManageKidsFragment mFragment;
    DecelerateInterpolator sDecelerator = new DecelerateInterpolator();
    OvershootInterpolator sOvershooter = new OvershootInterpolator(10f);
    private LayoutInflater mInflater;
    private Context mContext;

    public KidsListCursorAdapter(Context context, Cursor c, int flags, ManageKidsFragment fragment) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.kid_list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DbContract.Kids._ID));

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(cursor.getString(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME)));

        String pictureUri = cursor.getString(cursor
                .getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI));

        Bitmap profilePicture = null;
        if (pictureUri == null) {
            profilePicture = BitmapFactory.decodeResource(view.getResources(),
                    R.drawable.profile);
        } else {
            profilePicture = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictureUri),
                    THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        }
        CircularImageView profile = (CircularImageView) view.findViewById(R.id.profile);
        profile.setImageBitmap(profilePicture);

        TextView age = (TextView) view.findViewById(R.id.age);
        age.setText(Utils.getAge(cursor.getLong(cursor.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS))));

        int nWords = new PhraseSelection().getNumberOfWords(mContext.getContentResolver(), id);
        int nQAs = new PhraseSelection().getNumberOfQAs(mContext.getContentResolver(), id);

        TextView numOfPhrases = (TextView) view.findViewById(R.id.num_of_phrases);
        numOfPhrases.setText(nWords + " ");
       
        TextView numOfQAs = (TextView) view.findViewById(R.id.num_of_qas);
        numOfQAs.setText(nQAs + " ");

        ImageView edit = (ImageView) view.findViewById(R.id.icon_edit);
        edit.setTag(id);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddKidActivity.class);
                int id = (Integer) v.getTag();
                intent.putExtra(Prefs.CURRENT_KID_ID, id);
                mContext.startActivity(intent);
            }
        });

        ImageView delete = (ImageView) view.findViewById(R.id.icon_trash);
        delete.setTag(id);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.deleteItem((Integer) v.getTag());
            }
        });

    }
}
