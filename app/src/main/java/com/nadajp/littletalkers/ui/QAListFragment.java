package com.nadajp.littletalkers.ui;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.database.DbContract.Questions;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;

public class QAListFragment extends ItemListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int QA_LOADER = 20;
    private static final String[] LIST_COLUMNS = {
            Questions.COLUMN_NAME_QUESTION,
            Questions.COLUMN_NAME_ANSWER,
            Questions.COLUMN_NAME_DATE,
            Questions.COLUMN_NAME_AUDIO_FILE,
            Questions._ID,};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflateFragment(R.layout.fragment_qa_list, inflater, container, savedInstanceState);

        mPhraseColumnName = Questions.COLUMN_NAME_QUESTION;
        mEmptyListText = getString(R.string.no_qa);
        mEmptyListButtonText = getString(R.string.add_new_qa);
        mViewBinder = new ListRowViewBinder(mPlayer);

        mSortColumnId = Prefs.getSortColumnId(getActivity());
        mSortColumn = mSortColumnId == Prefs.SORT_COLUMN_DATE ? DbContract.Questions.COLUMN_NAME_DATE : mPhraseColumnName;

        int[] adapterRowViews = new int[]{R.id.question, R.id.answer,
                R.id.dictionary_word_date, R.id.dictionary_audio_button};

        if (mscAdapter == null) {
            mscAdapter = new SimpleCursorAdapter(this.getActivity(),
                    R.layout.qa_list_row, null, LIST_COLUMNS, adapterRowViews, 0);
        }
        mscAdapter.setViewBinder(mViewBinder);
        setListAdapter(mscAdapter);

        return v;
    }

    public void reloadData(){
        mSortColumn = mSortColumnId == Prefs.SORT_COLUMN_DATE ? DbContract.Questions.COLUMN_NAME_DATE : mPhraseColumnName;
        getLoaderManager().restartLoader(QA_LOADER, null, this);
    }


    @SuppressLint("NewApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Start out with a progress indicator.
        //setListShown(false);
        getLoaderManager().initLoader(QA_LOADER, null, this);
        mscAdapter.setViewBinder(mViewBinder);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void updateData(Kid kid) {
        super.updateData(kid);
        // Loader is restarted with new mCurrentKidId and
        // the list is automatically refreshed when it completes
        getLoaderManager().restartLoader(QA_LOADER, null, this);
    }

    public void deleteFromDatabase() {
        ContentResolver resolver = getActivity().getContentResolver();
        for (long id : mItemsToDelete) {
            String arg = Long.valueOf(id).toString();
            resolver.delete(Questions.CONTENT_URI, Questions._ID + " = ?", new String[]{arg});
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        if (id == QA_LOADER) {
            String sortOrder = mbSortAscending ? " ASC" : " DESC";

            CursorLoader loader = new CursorLoader(getActivity());

            if (mLanguage.equals(this.getActivity().getString(R.string.all_languages))) {
                loader.setUri(Questions.CONTENT_URI);
            } else {
                loader.setUri(Questions.buildQuestionsWithLanguageUri(mLanguage));
            }
            loader.setProjection(LIST_COLUMNS);
            loader.setSelection(Questions.COLUMN_NAME_KID + " = ?");
            loader.setSelectionArgs(new String[]{Long.valueOf(mCurrentKidId).toString()});
            loader.setSortOrder(mSortColumn + sortOrder);

            return loader;
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mscAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mscAdapter.swapCursor(null);
    }
}
