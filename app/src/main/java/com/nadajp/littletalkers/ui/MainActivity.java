package com.nadajp.littletalkers.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;

public class MainActivity extends Activity implements MainFragment.AddKidListener {

    // Constants
    private static String DEBUG_TAG = "Main Activity";

    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mType = -1;
        if (getIntent().hasExtra(Prefs.TYPE)) {
            mType = getIntent().getIntExtra(Prefs.TYPE, -1);
        }

        // Find out from shared preferences whether there are any kids yet
        int kidId = Prefs.getKidId(this, -1);
        // unless coming from widget, type from intent will be -1 so default to Word
        mType = mType == -1 ? Prefs.TYPE_WORD : mType;
        Prefs.saveType(this, mType);

        if (kidId > 0) {
            // on tablet, go to ItemListActivity
            if (getResources().getBoolean(R.bool.two_pane)) {
                Intent intent = new Intent(this, ItemListActivity.class);
                intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
                intent.putExtra(Prefs.TYPE, mType);
                startActivity(intent);
            } else { //on phone, go to AddItemActivity
                Intent intent = new Intent(this, AddItemActivity.class);
                intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
                intent.putExtra(Prefs.TYPE, mType);
                startActivity(intent);
            }
            finish();
        }
    }

    public void clickedAddKid() {
        Intent intent = new Intent(this, AddKidActivity.class);
        intent.putExtra(Prefs.CURRENT_KID_ID, -1);
        startActivity(intent);
        finish();
    }
}
