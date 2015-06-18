package cz.cvut.stepajin.feedreader.update;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;

import java.net.URL;
import java.util.List;

import cz.cvut.stepajin.feedreader.R;
import cz.cvut.stepajin.feedreader.data.FeedProviderContract;

public class RefreshTask extends AsyncTask<Void, Integer, Void> {

    public static final String STATE_CHANGED = "cz.cvut.steapjin.feedreader.refreshtask.statechanged";
    public static final String STATE = "cz.cvut.stepajin.feedreader.refreshtask.state";

    ContentResolver resolver;
    Context context;
    LocalBroadcastManager broadcaster;

    public RefreshTask(Context context) {
        this.context = context;
        this.resolver = context.getContentResolver();

        this.broadcaster = LocalBroadcastManager.getInstance(context);
    }

    public void broadcastStateChanged(AsyncTask.Status status) {
        Intent intent = new Intent(STATE_CHANGED);
        intent.putExtra(STATE, status);

        broadcaster.sendBroadcast(intent);
    }

    @Override
    protected void onPreExecute() {
        Log.d("RefreshTask", "begin");
        broadcastStateChanged(Status.RUNNING);
    }

    @Override
    protected Void doInBackground(Void... none) {

        Cursor feedsCursor = resolver.query(FeedProviderContract.CONTENT_URI_FEEDS, null, null, null, null);

        feedsCursor.moveToFirst();
        while (!feedsCursor.isAfterLast()) {
            int feedId = feedsCursor.getInt(
                    feedsCursor.getColumnIndexOrThrow(FeedProviderContract.FEED_ID));
            String link = feedsCursor.getString(
                    feedsCursor.getColumnIndex(FeedProviderContract.FEED_LINK));
            String title = feedsCursor.getString(
                    feedsCursor.getColumnIndex(FeedProviderContract.FEED_TITLE));

            retrieveFeed(link, feedId, title);

            feedsCursor.moveToNext();
        }
        feedsCursor.close();

        return null;
    }


    @Override
    protected void onPostExecute(Void none) {
        Log.d("RefreshTask", "ends");

        resolver.notifyChange(FeedProviderContract.CONTENT_URI_ENTRIES, null);

        broadcastStateChanged(Status.FINISHED);
    }

    @Override
    protected void onCancelled(Void none) {
        Log.d("RefreshTask", "canceled");
        broadcastStateChanged(Status.FINISHED);
    }


    private void retrieveFeed(String link, int feedId, String title) {
        try {
            Log.d("RefreshTask", "Url " + link + " Title " + title);

            URL feedUrl = new URL(link);

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            if (title.equals(context.getString(R.string.feed_title_unknown))) {
                Uri uri = Uri.parse(FeedProviderContract.CONTENT_URI_FEEDS.toString() + "/" + feedId);
                ContentValues values = new ContentValues();
                values.put(FeedProviderContract.FEED_TITLE, feed.getTitle());
                resolver.update(uri, values, null, null);
            }

            processFeedEntries(feed, feedId);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processFeedEntries(SyndFeed feed, int feedId) {
        List<SyndEntry> entries = feed.getEntries();

        Log.d("RefreshTask", "Retrieved " + entries.size() + " entries");

        ContentValues values = new ContentValues();

        for (SyndEntry e : entries) {

            values.put(FeedProviderContract.ENTRY_FEED_ID, feedId);
            values.put(FeedProviderContract.ENTRY_TITLE, e.getTitle());
            values.put(FeedProviderContract.ENTRY_DESCRIPTION, e.getDescription().getValue());
            if (e.getLink() != null) {
                values.put(FeedProviderContract.ENTRY_LINK, e.getLink());
            }

            if (e.getAuthor() != null) {
                values.put(FeedProviderContract.ENTRY_AUTHOR, e.getAuthor());
            }

            if (e.getUpdatedDate() != null) {
                values.put(FeedProviderContract.ENTRY_UPDATED, e.getUpdatedDate().toString());
            } else if (e.getPublishedDate() != null) {
                values.put(FeedProviderContract.ENTRY_UPDATED, e.getPublishedDate().toString());
            }

            Uri uri = resolver.insert(FeedProviderContract.CONTENT_URI_ENTRIES, values);

                /*if (uri != null) {
                    Log.d("db", "inserted " + e.getTitle());
                } else {
                    Log.d("db", "not inserted ");
                }*/
        }
    }
}
