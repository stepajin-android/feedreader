package cz.cvut.stepajin.feedreader.screens.feeds;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cz.cvut.stepajin.feedreader.R;
import cz.cvut.stepajin.feedreader.data.FeedProviderContract;

public class FeedsConfigurationListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FEEDS_LOADER = 0;

    FeedAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        adapter = new FeedAdapter(getActivity(), null);
        getLoaderManager().initLoader(FEEDS_LOADER, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Object tag = v.getTag();
        if (tag == null) {
            return;
        }

        int feedId = (int) tag;

        deleteFeedDialog(feedId);
    }

    /**
     * ******** Menu **********
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_feeds_configuration, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_feed:
                addFeedDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * ******** Feeds configuration **********
     */


    private void addFeedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_feed));

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String link = input.getText().toString();
                addFeed(link);
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addFeed(String link) {
        ContentResolver cr = getActivity().getContentResolver();

        ContentValues values = new ContentValues();
        values.put(FeedProviderContract.FEED_TITLE, getString(R.string.feed_title_unknown));
        values.put(FeedProviderContract.FEED_LINK, link);

        Uri uri = cr.insert(FeedProviderContract.CONTENT_URI_FEEDS, values);

        if (uri != null) {
            cr.notifyChange(FeedProviderContract.CONTENT_URI_FEEDS, null);
        }
    }

    private void deleteFeedDialog(final int feedId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.delete_feed));
        builder.setMessage(getString(R.string.delete_feed_confirm));

        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFeed(feedId);
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteFeed(final int feedId) {
        String uristr = FeedProviderContract.CONTENT_URI_FEEDS.toString() + "/" + feedId;
        Uri uri = Uri.parse(uristr);

        ContentResolver cr = getActivity().getContentResolver();
        int result = cr.delete(uri, null, null);

        cr.notifyChange(FeedProviderContract.CONTENT_URI_FEEDS, null);
        cr.notifyChange(FeedProviderContract.CONTENT_URI_ENTRIES, null);
    }


    /**
     ****** Loader callbacks *****
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;

        switch (id) {
            case FEEDS_LOADER:

                String[] projectionItems = {
                        FeedProviderContract.FEED_ID,
                        FeedProviderContract.FEED_TITLE,
                        FeedProviderContract.FEED_LINK
                };

                Uri uri = Uri.parse("content://" + FeedProviderContract.AUTHORITY + "/" + FeedProviderContract.FEEDS);
                cursorLoader = new CursorLoader(getActivity(), uri, projectionItems, null, null, null);
                return cursorLoader;

            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FEEDS_LOADER:
                cursor.setNotificationUri(getActivity().getContentResolver(), FeedProviderContract.CONTENT_URI_FEEDS);
                adapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    /**
     * ***** Cursor Adapter *******
     */

    private static class FeedAdapter extends CursorAdapter {

        FeedAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.item_list_entry, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView viewTitle = (TextView) view.findViewById(R.id.item_entry_title);
            TextView viewLink = (TextView) view.findViewById(R.id.item_entry_description);

            try {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.FEED_TITLE));
                String link = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.FEED_LINK));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedProviderContract.FEED_ID));

                viewTitle.setText(title);
                viewLink.setText(link);
                view.setTag(id);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

