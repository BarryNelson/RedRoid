package com.nelsoft.redroid.reddit;

import com.nelsoft.droiddit.reddit.model.RedditComment;
import com.nelsoft.droiddit.reddit.model.RedditLink;
import com.nelsoft.droiddit.reddit.model.RedditListing;
import com.nelsoft.droiddit.reddit.model.RedditMore;

public enum RedditType {
  t1(RedditComment.class),
  t3(RedditLink.class),
  Listing(RedditListing.class),
  more(RedditMore.class);

  private final Class mCls;

  RedditType(Class cls) {
    mCls = cls;
  }

  public Class getDerivedClass() {
    return mCls;
  }
}
