package com.nadajp.littletalkers.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ViewItemFragment extends ItemDetailFragment {
    private static final String DEBUG_TAG = "ViewItemFragment";
    private static final int SHARE_DIALOG_ID = 3;
    private OnUpdatePhraseListener mListener; // listener to notify activity that

    protected ShareActionProvider mShareActionProvider; // used to share data

    public static ViewItemFragment newInstance(int sectionNumber) {
        ViewItemFragment fragment;
        switch (sectionNumber) {
            case 1:
                fragment = new ViewQAFragment();
                break;
            default:
                fragment = new ViewWordFragment();
                break;
        }
        Bundle args = new Bundle();
        args.putInt(Prefs.TAB_ID, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public abstract long savePhrase(boolean automatic);

    public abstract String getShareBody();

    public abstract void updateExtraKidDetails();

    protected void insertData(View v) {
        mKidName = getArguments().getString(Prefs.KID_NAME);
        updateExtraKidDetails();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_item, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    // Handle presses on the action bar items
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                ShareDialog dlg = new ShareDialog();
                dlg.setTargetFragment(this, SHARE_DIALOG_ID);
                dlg.show(getFragmentManager(), ShareDialog.class.toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void saveItem(boolean exit) {
        super.saveItem(exit);
        if (mItemId > 0 && exit) {
            mListener.onPhraseUpdated(mCurrentKidId);
        }
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnUpdatePhraseListener) {
            mListener = (OnUpdatePhraseListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implemenet ViewItemFragment.OnUpdatePhraseListener");
        }
        super.onAttach(context);
    }

    // for API < 23
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnUpdatePhraseListener) {
            mListener = (OnUpdatePhraseListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet ViewItemFragment.OnUpdatePhraseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnUpdatePhraseListener {
        void onPhraseUpdated(int kidId);
    }

    public static class ShareDialog extends DialogFragment {
        private AppListAdapter mAdapter;
        private ArrayList<ComponentName> mComponents;
        private Intent mIntent;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setupSharing();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder
                    .setTitle(R.string.share)
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                    .setAdapter(mAdapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            mIntent.setComponent(mComponents.get(which));
                            getActivity().startActivity(mIntent);
                        }
                    });
            return builder.create();
        }

        private void setupSharing() {
            mIntent = new Intent(Intent.ACTION_SEND);
            File audioFile = ((ViewItemFragment) getTargetFragment())
                    .getAudioFile();

            if (audioFile != null) {
                Uri uri = Uri.fromFile(audioFile);
                mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mIntent.putExtra(Intent.EXTRA_STREAM, uri);
                mIntent.setType("audio/*");
            } else {
                mIntent.setType("text/plain");
            }

            mIntent.addCategory(Intent.CATEGORY_DEFAULT);
            mIntent.putExtra(Intent.EXTRA_SUBJECT,
                    (String) getString(R.string.app_name));
            mIntent.putExtra(Intent.EXTRA_TEXT,
                    ((ViewItemFragment) this.getTargetFragment()).getShareBody());

            PackageManager packageManager = getActivity().getPackageManager();

            List<ResolveInfo> activities = packageManager.queryIntentActivities(
                    mIntent, 0);
            ArrayList<String> appNames = new ArrayList<String>();
            ArrayList<Drawable> icons = new ArrayList<Drawable>();
            mComponents = new ArrayList<ComponentName>();

            for (ResolveInfo app : activities) {
                String name = app.loadLabel(packageManager).toString();
                //Log.i(DEBUG_TAG, "*" + name + "*" + "\n");
                if (!name.equals("Facebook")) {
                    mComponents.add(new ComponentName(
                            app.activityInfo.applicationInfo.packageName,
                            app.activityInfo.name));
                    appNames.add(app.loadLabel(packageManager).toString());
                    icons.add(app.loadIcon(packageManager));
                }
            }
            mAdapter = new AppListAdapter(getActivity(), appNames, icons);
        }

        public class AppListAdapter extends ArrayAdapter<String> {
            private final Context context;
            private final ArrayList<String> names;
            private final ArrayList<Drawable> icons;

            public AppListAdapter(Context context, ArrayList<String> names,
                                  ArrayList<Drawable> icons) {
                super(context, R.layout.app_list_row, names);
                this.context = context;
                this.names = names;
                this.icons = icons;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View rowView = inflater.inflate(R.layout.app_list_row, parent,
                        false);
                TextView textView = (TextView) rowView.findViewById(R.id.app_name);
                ImageView imageView = (ImageView) rowView
                        .findViewById(R.id.app_icon);

                textView.setText(names.get(position));
                // change the icon
                imageView.setImageDrawable(icons.get(position));
                return rowView;
            }
        }
    }
}
