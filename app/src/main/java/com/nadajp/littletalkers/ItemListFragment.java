package com.nadajp.littletalkers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.utils.Prefs;

public abstract class ItemListFragment extends ListFragment

{
    // to delete if delete icon pressed)
    private static final int DELETE_SELECTED_WORDS_DIALOG_ID = 1;
    private static final String DEBUG_TAG = "ItemListFragment";
    private static int mNumSelected = 0; // number of selected list items
    public int mCurrentKidId; // database id of current kid
    public String mSortColumn; // column to sort list by
    public String mLanguage; // current language
    // when list is empty
    public boolean mbSortAscending; // whether to sort list in ascending order
    // (selection mode begins with long
    // click)
    public long[] mItemsToDelete; // array of selected list items (will be used
    protected String mEmptyListText; // Text to display when list is empty
    protected String mEmptyListButtonText; // Text to display on Add New button
    protected MediaPlayer mPlayer; // audio player
    protected SimpleCursorAdapter mscAdapter;
    //private View mHeaderView;
    protected ListRowViewBinder mViewBinder;
    ListView mListView; // the list
    int mSortColumnId; // id of the column by which to sort, as defined in Prefs
    String mPhraseColumnName; // name of the main phrase column (i.e. word,
    // question)

    public static ItemListFragment newInstance(int sectionNumber) {
        ItemListFragment fragment;
        switch (sectionNumber) {
            case 1:
                fragment = new QAListFragment();
                break;
            default:
                fragment = new WordListFragment();
                break;
        }
        Bundle args = new Bundle();
        args.putInt(Prefs.TAB_ID, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    protected View inflateFragment(int resId, LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        View v = inflater.inflate(resId, container, false);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.list_layout);

        // get current kid id
        mCurrentKidId = getArguments().getInt(Prefs.CURRENT_KID_ID);
        mLanguage = getString(R.string.all_languages);
        // Now do the sorting by column
        mSortColumnId = Prefs.getSortColumnId(getActivity());
        if (mSortColumnId == Prefs.SORT_COLUMN_PHRASE) {
            mSortColumn = mPhraseColumnName;
        } else {
            mSortColumn = DbContract.Words.COLUMN_NAME_DATE;
        }
        mbSortAscending = Prefs.getIsAscending(getActivity());

        this.setHasOptionsMenu(true);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView tv = (TextView) getView().findViewById(R.id.no_words);
        tv.setText(mEmptyListText);

        mListView = getListView();
        setListAdapter(mscAdapter);

        // Implement contextual menu
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                if (!checked) {
                    mNumSelected--;
                } else {
                    mNumSelected++;
                }
                mode.setTitle("Selected: " + mNumSelected);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        mItemsToDelete = mListView.getCheckedItemIds();
                        deleteSelectedItems();
                        mode.finish(); // Action picked, so close the CAB
                        mNumSelected = 0;
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mNumSelected = 0;

                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are
                // deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
    }

    public void deleteSelectedItems() {
        DeleteSelectedDialogFragment dlg = new DeleteSelectedDialogFragment();
        dlg.setTargetFragment(this, DELETE_SELECTED_WORDS_DIALOG_ID);
        dlg.show(getFragmentManager(), "DeleteSelectedDialogFragment");
    }

    public abstract void deleteFromDatabase(); {}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // show word detail view
        Intent intent = new Intent(getActivity(), ViewItemActivity.class);
        intent.putExtra(Prefs.CURRENT_KID_ID, mCurrentKidId);
        intent.putExtra(ItemDetailFragment.ITEM_ID, id);
        int type = Prefs.getType(getActivity(), Prefs.TYPE_WORD);
        intent.putExtra(Prefs.TYPE, type);
        startActivity(intent);
    }

    public void changeLanguage(String language) {
        mLanguage = language;
        //Cursor newValues = getFromDatabase();
        //Log.i(DEBUG_TAG, "Items: " + newValues.getCount());
        //mscAdapter.swapCursor(newValues);
        //mscAdapter.notifyDataSetChanged();
    }

    public void updateData(int kidId) {
        mCurrentKidId = kidId;
    }

    public void sortByPhrase() {
        mSortColumnId = Prefs.SORT_COLUMN_PHRASE;
        mSortColumn = mPhraseColumnName;
        sortList();
    }

    public void sortByDate() {
        mSortColumnId = Prefs.SORT_COLUMN_DATE;
        mSortColumn = DbContract.Words.COLUMN_NAME_DATE;
        sortList();
    }

    private void sortList() {
        mbSortAscending = !mbSortAscending;
        //Cursor newValues = getFromDatabase();
        //mscAdapter.swapCursor(newValues);
        //mscAdapter.setViewBinder(mViewBinder);
        //mscAdapter.notifyDataSetChanged()
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Prefs.CURRENT_KID_ID, mCurrentKidId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayer = new MediaPlayer();
        mViewBinder.setMediaPlayer(mPlayer);
    }

    @Override
    public void onPause() {
        super.onPause();
        Prefs.saveAll(getActivity(), mCurrentKidId, mLanguage, mSortColumnId,
                mbSortAscending);

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public static class DeleteSelectedDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setMessage(R.string.delete_words_dialog)
                    .setPositiveButton(R.string.delete,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ((ItemListFragment) getTargetFragment())
                                            .deleteFromDatabase();
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
