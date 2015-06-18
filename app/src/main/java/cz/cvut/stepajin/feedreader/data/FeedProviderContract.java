package cz.cvut.stepajin.feedreader.data;

import android.net.Uri;

public class FeedProviderContract {
    public static final String AUTHORITY = "cz.cvut.stepajin.feedreader.provider";

    public static final String ENTRIES = "entries";
    public static final String FEEDS = "feeds";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_ENTRIES = Uri.parse(CONTENT_URI.toString() + "/" + ENTRIES);
    public static final Uri CONTENT_URI_FEEDS = Uri.parse(CONTENT_URI.toString() + "/" + FEEDS);

    public static final String ENTRY_ID = "_id";
    public static final String ENTRY_FEED_ID = "feedid";
    public static final String ENTRY_TITLE = "title";
    public static final String ENTRY_DESCRIPTION = "description";
    public static final String ENTRY_CONTENT = "content";
    public static final String ENTRY_LINK = "link";
    public static final String ENTRY_AUTHOR = "author";
    public static final String ENTRY_UPDATED = "updated";

    public static final String FEED_ID = "_id";
    public static final String FEED_LINK = "link";
    public static final String FEED_TITLE = "title";

    public static final String DEFAULT_SORT_ORDER_ENTRIES = "datetime(" + ENTRY_UPDATED + ")" + " DESC";
    public static final String DEFAULT_SORT_ORDER_FEEDS = FEED_ID + " ASC";

    public static final String MIME_TYPE_ITEM_ENTRY =
            "vnd.android.cursor.item/cz.cvut.stepajin.feedreader.entry";
    public static final String MIME_TYPE_DIR_ENTRY =
            "vnd.android.cursor.dir/cz.cvut.stepajin.feedreader.entry";
    public static final String MIME_TYPE_ITEM_FEED =
            "vnd.android.cursor.item/cz.cvut.stepajin.feedreader.feed";
    public static final String MIME_TYPE_DIR_FEED =
            "vnd.android.cursor.dir/cz.cvut.stepajin.feedreader.feed";
}
