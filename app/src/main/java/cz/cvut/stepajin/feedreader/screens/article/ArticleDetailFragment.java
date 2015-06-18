package cz.cvut.stepajin.feedreader.screens.article;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.internal.en;

import cz.cvut.stepajin.feedreader.R;
import cz.cvut.stepajin.feedreader.data.FeedProviderContract;
import cz.cvut.stepajin.feedreader.model.FeedEntry;


public class ArticleDetailFragment extends Fragment {

    public static final String ARG_ENTRY_ID = "entry_id";

    private int entryId = -1;
    private FeedEntry articleEntry;

    private TextView tvTitle;
    private TextView tvDateAuthor;
    private TextView tvText;
    private TextView tvLink;
    private ProgressBar progressBar;

    MenuItem menuItemShare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Log.d("frag", "0");
//        getActivity().getActionBar().setTitle(R.string.title_activity_entry_detail);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("frag", "0.5");

        return inflater.inflate(R.layout.fragment_article_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("frag", "1");

        tvTitle = (TextView) view.findViewById(R.id.article_detail_title);
        tvDateAuthor = (TextView) view.findViewById(R.id.article_detail_date_author);
        tvText = (TextView) view.findViewById(R.id.article_detail_text);
        tvLink = (TextView) view.findViewById(R.id.article_detail_link);
        progressBar = (ProgressBar) view.findViewById(R.id.article_detail_progressBar);

        Log.d("frag", "2");

        Bundle args = getArguments();

        Log.d("frag", "3");

        if (args == null || args.getInt(ARG_ENTRY_ID, -1) == -1) {
            showProgressBar(true);
            return;
        }

        Log.d("frag", "4");

        loadFragment(args.getInt(ARG_ENTRY_ID, -1));

        Log.d("frag", "5");
    }

    public void loadFragment(int entryId) {
        Log.d("frag", "load " + entryId);

        this.entryId = entryId;

        LoadArticleTask task = new LoadArticleTask();
        task.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_entry_detail, menu);

        menuItemShare = menu.findItem(R.id.action_share);
        menuItemShare.setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareArticle();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showProgressBar(boolean loading) {
        int articleVisibility = loading ? View.INVISIBLE : View.VISIBLE;
        int progressBarVisibility = loading ? View.VISIBLE : View.INVISIBLE;

        tvTitle.setVisibility(articleVisibility);
        tvDateAuthor.setVisibility(articleVisibility);
        tvText.setVisibility(articleVisibility);
        tvLink.setVisibility(articleVisibility);
        progressBar.setVisibility(progressBarVisibility);
    }

    private void shareArticle() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String text = articleEntry.getTitle() + "\n\n" + articleEntry.getLink();
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));
    }

    private class LoadArticleTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.d("frag", "show");
            showProgressBar(true);

            Log.d("frag", "showed");
        }

        @Override
        protected Void doInBackground(Void... params) {
            ContentResolver cr = getActivity().getContentResolver();
            Uri uri = Uri.parse(FeedProviderContract.CONTENT_URI_ENTRIES + "/" + entryId);
            Cursor cursor = cr.query(uri, null, null, null, null);

            cursor.moveToFirst();

            if (cursor.getCount() != 1) {
                return null;
            }

            String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_DESCRIPTION));
            String id = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_ID));
            String link = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_LINK));

            articleEntry = new FeedEntry(title, id, link);
            articleEntry.setDescription(description);

            try {
                String author = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_AUTHOR));
                articleEntry.setAuthor(author);

                String updated = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_UPDATED));
                articleEntry.setUpdated(updated);

            } catch (Exception e) {
                System.out.println(e);
            }

            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void none) {
            if (articleEntry != null) {

                tvTitle.setText(Html.fromHtml(articleEntry.getTitle()));

                if (articleEntry.getAuthor() == null || articleEntry.getAuthor().equals("")) {
                    articleEntry.setAuthor(getString(R.string.unknown_author));
                }

                tvText.setText(Html.fromHtml(articleEntry.getDescription()));

                tvDateAuthor.setText(articleEntry.getUpdated() + " " +
                        getString(R.string.by) + " " + articleEntry.getAuthor());

                String link = "<a href='" + articleEntry.getLink() + "'>" + getString(R.string.view_full_article).toUpperCase() + "</a>";
                tvLink.setClickable(true);
                tvLink.setMovementMethod(LinkMovementMethod.getInstance());
                tvLink.setText(Html.fromHtml(link));

            } else {
                tvTitle.setText(R.string.entry_not_found);
                tvDateAuthor.setVisibility(View.INVISIBLE);
                tvText.setText(R.string.entry_not_found);
            }

            showProgressBar(false);
            menuItemShare.setEnabled(true);

        }
    }
}
