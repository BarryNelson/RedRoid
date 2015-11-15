package com.nelsoft.redroid.reddit;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nelsoft.redroid.reddit.model.RedditListing;
import com.nelsoft.redroid.reddit.model.RedditObject;
import com.nelsoft.redroid.reddit.model.RedditResponse;

import org.joda.time.DateTime;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface RedditService {
    @GET("/r/{subreddit}/comments/{id}.json")
    List<RedditResponse<RedditListing>> getComments(
            @Path("subreddit") String subreddit,
            @Path("id") String id
    );

    @GET("/r/{subreddit}/comments/{id}.json")
    void getComments(
            @Path("subreddit") String subreddit,
            @Path("id") String id,
            Callback<List<RedditResponse<RedditListing>>> callback
    );

    @GET("/r/{subreddit}/.json")
    RedditResponse<RedditListing> getSubreddit(
            @Path("subreddit") String subreddit);

    @GET("/r/{subreddit}/.json")
    void getSubreddit(
            @Path("subreddit") String subreddit,
            Callback<RedditResponse<RedditListing>> callback);

    // example: getSubreddit("funny", "after=t3_3sjcxx"); // the ? will be added by @Query
    @GET("/r/{subreddit}/.json")
    void getSubredditAfter(
            @Path("subreddit") String subreddit,
            @Query("after") String param,
            Callback<RedditResponse<RedditListing>> callback
    );

    public static class Implementation {
        public static RedditService get() {
            return getBuilder()
                    .build()
                    .create(RedditService.class);
        }

        static RestAdapter.Builder getBuilder() {
            return new RestAdapter
                    .Builder()
                    .setConverter(new GsonConverter(getGson()))
                    .setEndpoint("http://www.reddit.com");
        }

        private static Gson getGson() {
            return new GsonBuilder()
                    .registerTypeAdapter(RedditObject.class, new RedditObjectDeserializer())
                    .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                    .create();
        }
    }
}
