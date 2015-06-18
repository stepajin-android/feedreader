package cz.cvut.stepajin.feedreader.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class FeedContentProvider extends ContentProvider {

    private static final int ENTRIES = 0;
    private static final int ENTRY_ID = 1;
    private static final int FEEDS = 2;
    private static final int FEED_ID = 3;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(FeedProviderContract.AUTHORITY, FeedProviderContract.ENTRIES, ENTRIES);
        uriMatcher.addURI(FeedProviderContract.AUTHORITY, FeedProviderContract.ENTRIES + "/#", ENTRY_ID);
        uriMatcher.addURI(FeedProviderContract.AUTHORITY, FeedProviderContract.FEEDS, FEEDS);
        uriMatcher.addURI(FeedProviderContract.AUTHORITY, FeedProviderContract.FEEDS + "/#", FEED_ID);
    }

    FeedDatabaseOpenHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new FeedDatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ENTRIES:
                return FeedProviderContract.MIME_TYPE_DIR_ENTRY;

            case ENTRY_ID:
                return FeedProviderContract.MIME_TYPE_ITEM_ENTRY;

            case FEEDS:
                return FeedProviderContract.MIME_TYPE_DIR_FEED;

            case FEED_ID:
                return FeedProviderContract.MIME_TYPE_ITEM_FEED;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriId = uriMatcher.match(uri);

        if (uriId == ENTRIES || uriId == ENTRY_ID) {
            queryBuilder.setTables(FeedDatabaseOpenHelper.TABLE_ENTRIES);
            if (sortOrder == null || sortOrder.equals(""))
                sortOrder = FeedProviderContract.DEFAULT_SORT_ORDER_ENTRIES;


            if (uriId == ENTRY_ID) {
                queryBuilder.appendWhere(FeedDatabaseOpenHelper.ENTRY_ID + "="
                        + uri.getLastPathSegment());
            }

        } else if (uriId == FEEDS || uriId == FEED_ID) {
            queryBuilder.setTables(FeedDatabaseOpenHelper.TABLE_FEEDS);
            if (sortOrder == null || sortOrder.equals(""))
                sortOrder = FeedProviderContract.DEFAULT_SORT_ORDER_FEEDS;

            if (uriId == FEED_ID) {
                queryBuilder.appendWhere(FeedDatabaseOpenHelper.FEED_ID + "="
                        + uri.getLastPathSegment());
            }

        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        return cursor;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch ( uriMatcher.match(uri) ) {
            case ENTRIES:
                // TODO
                return 0;
            case FEEDS:
                // TODO
                return 0;

            case FEED_ID:
                return deleteFeed(uri);

            case ENTRY_ID:
                // TODO
                return 0;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private int deleteFeed(Uri uri) {
        String feedId = uri.getLastPathSegment();

        Log.d("Delete feed ", feedId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FeedDatabaseOpenHelper.TABLE_FEEDS, FeedProviderContract.FEED_ID + "=?", new String[]{feedId});
        int result = db.delete(FeedDatabaseOpenHelper.TABLE_ENTRIES, FeedProviderContract.ENTRY_FEED_ID + "=?", new String[]{feedId});

        Log.d("delete feed", "deleted " + result + " entries");

        return result;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch ( uriMatcher.match(uri) ) {
            case ENTRIES:
                return insertEntry(uri, values);
            case FEEDS:
                return insertFeed(uri, values);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

    }

    private Uri insertEntry(Uri uri, ContentValues values) {
        if (!values.containsKey(FeedProviderContract.ENTRY_TITLE)
                || !values.containsKey(FeedProviderContract.ENTRY_LINK)
                || !values.containsKey(FeedProviderContract.ENTRY_FEED_ID)) {
            throw new IllegalArgumentException("Inserting wrong values to FeedContentProvider");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            long id = db.insertOrThrow(FeedDatabaseOpenHelper.TABLE_ENTRIES, null, values);
            if (id > 0) {
                return ContentUris.withAppendedId(FeedProviderContract.CONTENT_URI_ENTRIES, id);
            } else {
                return null;
            }
        } catch (SQLiteConstraintException e) {
            //Log.d("db", "Not unique " + values.getAsString(FeedProviderContract.ENTRY_LINK));
            return null;
        }
    }

    private Uri insertFeed(Uri uri, ContentValues values) {
        if (!values.containsKey(FeedProviderContract.FEED_LINK)) {
            throw new IllegalArgumentException("Inserting wrong values to FeedContentProvider");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            long id = db.insertOrThrow(FeedDatabaseOpenHelper.TABLE_FEEDS, null, values);
            if (id > 0) {
                return ContentUris.withAppendedId(FeedProviderContract.CONTENT_URI_ENTRIES, id);
            } else {
                return null;
            }
        } catch (SQLiteConstraintException e) {
            //Log.d("db", "Not unique " + values.getAsString(FeedProviderContract.FEED_LINK));
            return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch ( uriMatcher.match(uri) ) {
            case FEED_ID:
                return updateFeed(uri, values);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private int updateFeed(Uri uri, ContentValues values) {
        String feedId = uri.getLastPathSegment();

        Log.d("Update feed ", feedId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(FeedDatabaseOpenHelper.TABLE_FEEDS,values, FeedProviderContract.FEED_ID + "=?", new String[]{feedId});
    }
}
