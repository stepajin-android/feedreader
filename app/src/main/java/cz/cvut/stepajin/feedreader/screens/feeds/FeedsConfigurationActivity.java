package cz.cvut.stepajin.feedreader.screens.feeds;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import cz.cvut.stepajin.feedreader.R;

public class FeedsConfigurationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}