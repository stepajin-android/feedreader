package cz.cvut.stepajin.feedreader.screens.main;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cz.cvut.stepajin.feedreader.R;
import cz.cvut.stepajin.feedreader.screens.feeds.FeedsConfigurationActivity;
import cz.cvut.stepajin.feedreader.update.RefreshService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RefreshService.schedule(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_configure_feeds:
                Intent intent = new Intent(this, FeedsConfigurationActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}