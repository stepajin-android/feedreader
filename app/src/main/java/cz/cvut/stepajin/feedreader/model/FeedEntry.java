package cz.cvut.stepajin.feedreader.model;

/**
 * Simple Feed entry representation.
 *
 * @author Ondrej Cermak
 */
public class FeedEntry {
    private Feed mFeed;
    private String mTitle;
    private String mId;
    private String mLink;
    private String mUpdated;
    private String mDescription;
    private String mContent;

    private String mAuthor;
    private String mAuthorEmail;

    public FeedEntry(String title, String id, String link) {
        mTitle = title;
        mId = id;
        mLink = link;

        mFeed = null;
        mDescription = "";
        mUpdated = "never";
        mContent = "";
        mAuthor = "";
        mAuthorEmail = "";
    }

    public Feed getFeed() {
        return mFeed;
    }

    public void setFeed(Feed feed) {
        mFeed = feed;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getUpdated() {
        return mUpdated;
    }

    public long getUpdatedAsLong() {
        // TODO
        // Simpledateformat atd.
        return 12345l;
    }

    public void setUpdated(String updated) {
        mUpdated = updated;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAuthorEmail() {
        return mAuthorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        mAuthorEmail = authorEmail;
    }
}
