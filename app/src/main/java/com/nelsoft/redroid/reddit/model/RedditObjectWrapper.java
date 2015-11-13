package com.nelsoft.redroid.reddit.model;

import com.google.gson.JsonElement;
import com.nelsoft.redroid.reddit.RedditType;

public class RedditObjectWrapper {
  RedditType kind;
  JsonElement data;

  public RedditType getKind() {
    return kind;
  }

    public JsonElement getData() {
        return data;
    }
}