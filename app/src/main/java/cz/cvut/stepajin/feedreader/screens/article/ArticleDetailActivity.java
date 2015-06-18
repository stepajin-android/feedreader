package cz.cvut.stepajin.feedreader.screens.article;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import cz.cvut.stepajin.feedreader.R;

public class ArticleDetailActivity extends Activity {
   // public static final String EXTRA_FEED_ID = "feed_id";
    public static final String EXTRA_ENTRY_ID = "entry_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        addArticleDetailFragment();
    }

    private void addArticleDetailFragment() {
        FragmentManager manager = getFragmentManager();
        if (manager.findFragmentById(R.id.container) == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.container, createArticleDetailFragment());
            transaction.commit();
        }
    }

    private Fragment createArticleDetailFragment() {
        Bundle args = new Bundle();
        /*args.putString(ArticleDetailFragment.ARG_FEED_ID,
                getIntent().getStringExtra(EXTRA_FEED_ID));*/
        args.putInt(ArticleDetailFragment.ARG_ENTRY_ID,
                getIntent().getIntExtra(EXTRA_ENTRY_ID, 0));
        return Fragment.instantiate(this, ArticleDetailFragment.class.getName(), args);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT < 16) {
                    // This is not the right solution, but will have to be enough for now.
                    finish();
                    return true;
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
