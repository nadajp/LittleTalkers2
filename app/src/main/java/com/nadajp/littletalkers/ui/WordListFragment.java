package com.nadajp.littletalkers.ui;

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
import com.nadajp.littletalkers.database.DbContract.Words;
import com.nadajp.littletalkers.model.Kid;
import com.nadajp.littletalkers.utils.Prefs;

public class WordListFragment extends ItemListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String DEBUG_TAG = "WordListFragment";
    private static final int WORDS_LOADER = 100;
    private static final String[] DICTIONARY_COLUMNS = {Words.COLUMN_NAME_WORD,
            Words.COLUMN_NAME_DATE,
            Words.COLUMN_NAME_AUDIO_FILE,
            Words._ID};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflateFragment(R.layout.fragment_dictionary, inflater, container, savedInstanceState);

        mPhraseColumnName = Words.COLUMN_NAME_WORD;
        mEmptyListText = getString(R.string.no_words);
        mEmptyListButtonText = getString(R.string.add_word);

        mViewBinder = new ListRowViewBinder(mPlayer);

        mSortColumnId = Prefs.getSortColumnId(getActivity());
        mSortColumn = mSortColumnId == Prefs.SORT_COLUMN_DATE ? Words.COLUMN_NAME_DATE : mPhraseColumnName;

        String[] adapterCols = new String[]{Words.COLUMN_NAME_WORD,
                Words.COLUMN_NAME_DATE,
                Words.COLUMN_NAME_AUDIO_FILE};

        int[] adapterRowViews = new int[]{R.id.dictionary_word,
                R.id.dictionary_word_date, R.id.dictionary_audio_button};

        if (mscAdapter == null) {
            mscAdapter = new SimpleCursorAdapter(this.getActivity(),
                    R.layout.dictionary_row, null, adapterCols, adapterRowViews, 0);
        }
        mscAdapter.setViewBinder(mViewBinder);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(WORDS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void updateData(Kid kid) {
        super.updateData(kid);
        // Loader is restarted with new mCurrentKidId and
        // the list is automatically refreshed when it completes
        getLoaderManager().restartLoader(WORDS_LOADER, null, this);
    }

    public void deleteFromDatabase() {
        ContentResolver resolver = getActivity().getContentResolver();
        for (long id : mItemsToDelete) {
            String arg = Long.valueOf(id).toString();
            resolver.delete(Words.CONTENT_URI, Words._ID + " = ?", new String[]{arg});
        }
    }

    public void reloadData(){
        mSortColumn = mSortColumnId == Prefs.SORT_COLUMN_DATE ? Words.COLUMN_NAME_DATE : mPhraseColumnName;
        getLoaderManager().restartLoader(WORDS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        String sortOrder = mbSortAscending ? " ASC" : " DESC";

        CursorLoader loader = new CursorLoader(getActivity());

        if (mLanguage.equals(this.getActivity().getString(R.string.all_languages))) {
            loader.setUri(Words.CONTENT_URI);
        } else {
            loader.setUri(Words.buildWordsWithLanguageUri(mLanguage));
        }
        loader.setProjection(DICTIONARY_COLUMNS);
        loader.setSelection(Words.COLUMN_NAME_KID + " = ?");
        loader.setSelectionArgs(new String[]{Long.valueOf(mCurrentKidId).toString()});
        loader.setSortOrder(mSortColumn + sortOrder);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mscAdapter.swapCursor(data);
        mListView.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mscAdapter.swapCursor(null);
    }
}