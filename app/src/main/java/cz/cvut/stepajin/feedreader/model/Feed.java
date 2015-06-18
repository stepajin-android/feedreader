package cz.cvut.stepajin.feedreader.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Feed representation.
 *
 * @author Ondrej Cermak
 */
public class Feed {
  private String mId;
  private String mLink;
  private String mTitle;
  private String mSubtitle;
  private long mUpdated;
  private String mAuthor;
  private String mAuthorEmail;

  private List<FeedEntry> mEntries;

  public Feed(String id, String link, String title) {
    mId = id;
    mLink = link;
    mTitle = title;
    mEntries = new ArrayList<FeedEntry>();
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

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    mTitle = title;
  }

  public String getSubtitle() {
    return mSubtitle;
  }

  public void setSubtitle(String subtitle) {
    mSubtitle = subtitle;
  }

  public long getUpdated() {
    return mUpdated;
  }

  public void setUpdated(long updated) {
    mUpdated = updated;
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

  public List<FeedEntry> getEntries() {
    return mEntries;
  }
}
