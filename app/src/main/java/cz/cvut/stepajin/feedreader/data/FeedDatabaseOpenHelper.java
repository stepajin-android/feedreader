package cz.cvut.stepajin.feedreader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FeedDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final String ENTRIES_DB_NAME = "db";

    public static final String TABLE_ENTRIES = "entries";
    public static final String ENTRY_ID = FeedProviderContract.ENTRY_ID;
    public static final String ENTRY_FEED_ID = FeedProviderContract.ENTRY_FEED_ID;
    public static final String ENTRY_TITLE = FeedProviderContract.ENTRY_TITLE;
    public static final String ENTRY_DESCRIPTION = FeedProviderContract.ENTRY_DESCRIPTION;
    public static final String ENTRY_CONTENT = FeedProviderContract.ENTRY_CONTENT;
    public static final String ENTRY_LINK = FeedProviderContract.ENTRY_LINK;
    public static final String ENTRY_AUTHOR = FeedProviderContract.ENTRY_AUTHOR;
    public static final String ENTRY_UPDATED = FeedProviderContract.ENTRY_UPDATED;

    public static final String TABLE_FEEDS = "feeds";
    public static final String FEED_ID = FeedProviderContract.FEED_ID;
    public static final String FEED_LINK = FeedProviderContract.FEED_LINK;
    public static final String FEED_TITLE = FeedProviderContract.FEED_TITLE;


    public FeedDatabaseOpenHelper(Context context) {
        super(context, ENTRIES_DB_NAME, null, 42);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_ENTRIES + " (" +
                ENTRY_ID + " integer primary key autoincrement," +
                ENTRY_FEED_ID + " integer," +
                ENTRY_TITLE + " text," +
                ENTRY_DESCRIPTION + " text," +
                ENTRY_CONTENT + " text," +
                ENTRY_LINK + " text NOT NULL UNIQUE," +
                ENTRY_AUTHOR + " text," +
                ENTRY_UPDATED + " text" +
                ");");

        db.execSQL("create table " + TABLE_FEEDS + " (" +
                FEED_ID + " integer primary key autoincrement," +
                FEED_TITLE + " text," +
                FEED_LINK + " text NOT NULL UNIQUE" +
                ");");

        ContentValues cv = new ContentValues();
        cv.put(FEED_TITLE, "Novinky");
        cv.put(FEED_LINK, "http://www.novinky.cz/rss/");
        db.insert(TABLE_FEEDS, null, cv);
        cv.put(FEED_TITLE, "Eurofotbal");
        cv.put(FEED_LINK, "http://www.eurofotbal.cz/feed/rss/");
        db.insert(TABLE_FEEDS, null, cv);

        Log.d("db", "database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("" +
                "db", "Aktualizace databaze - puvodni verze: " + oldVersion + ", nova verze: " + newVersion);

        db.execSQL("drop table if exists " + TABLE_ENTRIES);
        db.execSQL("drop table if exists " + TABLE_FEEDS);

        onCreate(db);
    }
}
