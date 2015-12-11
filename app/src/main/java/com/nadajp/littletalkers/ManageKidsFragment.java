package com.nadajp.littletalkers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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

import com.nadajp.littletalkers.database.DbContract;
import com.nadajp.littletalkers.model.Kid;

import java.util.ArrayList;

public class ManageKidsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DELETE_SELECTED_DIALOG_ID = 1;
    private static final int KIDS_LOADER = 5;
    private static int mNumSelected = 0;
    private static String DEBUG_TAG = "ManageKids";
    public long[] mItemsToDelete;
    ListView listView;
    KidsListCursorAdapter mAdapter;
    private ModifyKidsListener mListener;
    private ArrayList<Kid> mKids = new ArrayList<Kid>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_kids, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(KIDS_LOADER, null, this);

        // Create empty list adapter, will be filled in later by loader
        mAdapter = new KidsListCursorAdapter(getActivity(), null, 0, this);
        setListAdapter(mAdapter);

        // Implement contextual menu
        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                if (!checked) {
                    mNumSelected--;
                } else {
                    mNumSelected++;
                    listView.setSelection(position);
                }
                mode.setTitle("Selected: " + mNumSelected);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        mItemsToDelete = listView.getCheckedItemIds();
                        //Log.i(DEBUG_TAG, "Items to delete: " + mItemsToDelete.length);
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
        Bundle args = new Bundle();
        args.putLong("id", -1);
        dlg.setArguments(args);
        dlg.setTargetFragment(this, DELETE_SELECTED_DIALOG_ID);
        dlg.show(getFragmentManager(), "DeleteSelectedDialogFragment");
    }

    public void deleteItem(long id) {
        DeleteSelectedDialogFragment dlg = new DeleteSelectedDialogFragment();
        Bundle args = new Bundle();
        args.putLong("id", id);
        dlg.setArguments(args);
        dlg.setTargetFragment(this, DELETE_SELECTED_DIALOG_ID);
        dlg.show(getFragmentManager(), "DeleteSelectedDialogFragment");
    }

    public void confirmDelete(long singleId) {
        if (singleId > -1) {
            mItemsToDelete = new long[1];
            mItemsToDelete[0] = singleId;
        }

        ContentResolver resolver = getActivity().getContentResolver();
        for (long id : mItemsToDelete) {
            String arg = Long.valueOf(id).toString();
            resolver.delete(DbContract.Kids.CONTENT_URI,
                    DbContract.Kids._ID + " = ?", new String[]{arg});
            resolver.delete(DbContract.Words.CONTENT_URI,
                    DbContract.Words.COLUMN_NAME_KID + " = ?", new String[]{arg});
            resolver.delete(DbContract.Questions.CONTENT_URI,
                    DbContract.Questions.COLUMN_NAME_KID + " = ?", new String[]{arg});
        }
        mListener.onKidsDeleted();
        mItemsToDelete = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // show kid detail view
        Intent intent = new Intent(this.getActivity(), KidProfileActivity.class);
        intent.putExtra(getString(R.string.kid_details), mKids.get(position));
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == KIDS_LOADER) {
            final String[] PROJECTION = new String[]{
                    DbContract.Kids._ID,
                    DbContract.Kids.COLUMN_NAME_NAME,
                    DbContract.Kids.COLUMN_NAME_PICTURE_URI,
                    DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS
            };
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(getActivity(), DbContract.Kids.CONTENT_URI,
                    null, null, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        int i = 0;
        // store all the data for easy retrieval
        data.moveToFirst();

        while (!data.isAfterLast()) {
            Kid kid = new Kid(data.getInt(data.getColumnIndex(DbContract.Kids._ID)),
                    data.getString(data.getColumnIndex(DbContract.Kids.COLUMN_NAME_NAME)),
                    data.getString(data.getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LOCATION)),
                    data.getString(data.getColumnIndex(DbContract.Kids.COLUMN_NAME_DEFAULT_LANGUAGE)),
                    data.getString(data.getColumnIndex(DbContract.Kids.COLUMN_NAME_PICTURE_URI)),
                    data.getLong(data.getColumnIndex(DbContract.Kids.COLUMN_NAME_BIRTHDATE_MILLIS)));

            mKids.add(i++, kid);
            data.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ModifyKidsListener) {
            mListener = (ModifyKidsListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet ManageKidsFragment.ModifyKidsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ModifyKidsListener {
        public void onKidsDeleted();
    }

    public static class DeleteSelectedDialogFragment extends DialogFragment {
        public long mId;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mId = getArguments().getLong("id");

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setMessage(R.string.delete_kids_dialog)
                    .setPositiveButton(R.string.delete,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ((ManageKidsFragment) getTargetFragment())
                                            .confirmDelete(mId);
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