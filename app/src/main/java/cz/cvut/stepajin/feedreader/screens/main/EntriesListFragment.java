package cz.cvut.stepajin.feedreader.screens.main;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cz.cvut.stepajin.feedreader.R;
import cz.cvut.stepajin.feedreader.data.FeedProviderContract;
import cz.cvut.stepajin.feedreader.screens.article.ArticleDetailFragment;
import cz.cvut.stepajin.feedreader.update.RefreshTask;
import cz.cvut.stepajin.feedreader.screens.article.ArticleDetailActivity;

public class EntriesListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ENTRIES_LOADER = 0;

    private FeedAdapter adapter;
    private Menu menu;

    private static RefreshTask task = null;
    private RefreshTaskStateChangeReceiver receiver;

    /*******************
     *
     *  Lifecycle
     *
     ******************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        adapter = new FeedAdapter(getActivity(), null);
        receiver = new RefreshTaskStateChangeReceiver();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Load the entries from the database.
        getLoaderManager().initLoader(ENTRIES_LOADER, null, this);

        // Refresh the entries when the app starts.
        if (task == null) {
         //   executeTask();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(receiver, new IntentFilter(RefreshTask.STATE_CHANGED));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (task != null) {
            task.cancel(true);
        //    task = null;
        }
    }


    /**************
     *
     *  Menu
     *
     **************/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.menu_main_fragment, menu);

        if (task != null && task.getStatus().equals(AsyncTask.Status.RUNNING)) {
            Log.d("Refresh", "running");
            setRefreshActionButtonState(true);
        } else {
            Log.d("Refresh", "finished or none");
            setRefreshActionButtonState(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh:
                executeTask();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (menu != null) {
            final MenuItem refreshItem = menu
                    .findItem(R.id.action_refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.view_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }


    /****************
     *
     *  Methods
     *
     *****************/

    public void executeTask() {
        task = new RefreshTask(getActivity());
        task.execute();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ArticleDetailFragment articleFrag = (ArticleDetailFragment)
                getFragmentManager().findFragmentById(R.id.fragment_entry_detail);

        if (articleFrag != null && articleFrag.isInLayout()) {
            articleFrag.loadFragment((int) id);

        } else {
            Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.EXTRA_ENTRY_ID, (int) id);

            getActivity().startActivity(intent);

        }
    }


    /**************
     *
     *  Receiver
     *
     **************/

    private class RefreshTaskStateChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            RefreshTask.Status status = (RefreshTask.Status) intent.getSerializableExtra(RefreshTask.STATE);

            if (status.equals(RefreshTask.Status.RUNNING)) {
                setRefreshActionButtonState(true);
            } else {
                setRefreshActionButtonState(false);
            }
        }
    }


    /***********************
     *
     *  Loader callbacks
     *
     ***********************/


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;

        switch (id) {
            case ENTRIES_LOADER:
                String[] projectionItems = {
                        FeedProviderContract.ENTRY_ID,
                        FeedProviderContract.ENTRY_DESCRIPTION,
                        FeedProviderContract.ENTRY_TITLE,
                        FeedProviderContract.ENTRY_UPDATED,
                        FeedProviderContract.ENTRY_LINK
                };

                Uri uri = FeedProviderContract.CONTENT_URI_ENTRIES;
                String sortBy = FeedProviderContract.DEFAULT_SORT_ORDER_ENTRIES;
                cursorLoader = new CursorLoader(getActivity(), uri, projectionItems, null, null, sortBy);
                return cursorLoader;

            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case ENTRIES_LOADER:
                cursor.setNotificationUri(getActivity().getContentResolver(), FeedProviderContract.CONTENT_URI_ENTRIES);
                adapter.swapCursor(cursor);

                ArticleDetailFragment articleFrag = (ArticleDetailFragment)
                        getFragmentManager().findFragmentById(R.id.fragment_entry_detail);

                if (articleFrag != null && articleFrag.isInLayout()) {
                    Log.d("frag", "existuje");
                    articleFrag.loadFragment(cursor.getInt(cursor.getColumnIndex("_id")));
                } else {
                    Log.d("frag", "neexistuje");
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    /*********************
     *
     *   Cursor adapter
     *
     *********************/

    private static class FeedAdapter extends CursorAdapter {

        private static class ViewHolder {
            TextView title;
            TextView description;

            ViewHolder(View view) {
                title = (TextView) view.findViewById(R.id.item_entry_title);
                description = (TextView) view.findViewById(R.id.item_entry_description);
            }
        }


        FeedAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_list_entry, parent, false);
            v.setTag(new ViewHolder(v));
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ViewHolder holder = (ViewHolder) view.getTag();

            try {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_DESCRIPTION));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedProviderContract.ENTRY_ID));

                holder.title.setText(title);
                holder.description.setText(description);
                //  view.setTag(id);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}

