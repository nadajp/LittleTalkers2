package com.nadajp.littletalkers.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.nadajp.littletalkers.R;
import com.nadajp.littletalkers.utils.Prefs;

public class MainActivity extends Activity implements MainFragment.AddKidListener {

    // Constants
    private static String DEBUG_TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find out from shared preferences whether there are any kids yet
        int kidId = Prefs.getKidId(this, -1);

        if (kidId > 0) {
        // Go to ItemListActivity
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra(Prefs.CURRENT_KID_ID, kidId);
            intent.putExtra(Prefs.ADD_TYPE, Prefs.TYPE_WORD);
            startActivity(intent);
        }
    }

    public void clickedAddKid() {
        Intent intent = new Intent(this, AddKidActivity.class);
        intent.putExtra(Prefs.CURRENT_KID_ID, -1);
        startActivity(intent);
    }
}
