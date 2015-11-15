package com.nelsoft.redroid.reddit_search;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.nelsoft.redroid.R;
import com.nelsoft.redroid.reddit.RedditService;
import com.nelsoft.redroid.reddit.model.RedditLink;
import com.nelsoft.redroid.reddit.model.RedditListing;
import com.nelsoft.redroid.reddit.model.RedditObject;
import com.nelsoft.redroid.reddit.model.RedditResponse;

import java.util.ArrayList;
import java.util.ListIterator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RedditSearchFragment extends Fragment {
    
    private static final String TAG = "RedditSearchFragment";

    private OnRedditSearchFragmentListener mListener;
    private Activity activity;
    private View view;

    ArrayList<RedditLink> redditPostingList = new ArrayList<>();
    private EditText searchValue;
    private String subreddit;
    private RecyclerView redditRecyclerView;
    private RedditRecyclerAdapter redditRecyclerAdapter;
    private LinearLayoutManager layoutManager;
    private RedditResponse<RedditListing> listing;
    private String after = null;
    private String lastSearch;
    private Callback<RedditResponse<RedditListing>> redditCallback;
    private boolean searching = false;

    public RedditSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_reddit_search, container, false);
        initControls();
        doSearch(searchValue.getText().toString(), null);
        return view;
    }

    private void initControls() {

        searchValue = (EditText) view.findViewById(R.id.text_search);

        ((ImageView) view.findViewById(R.id.btn_Search)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doSearch(searchValue.getText().toString(), null);
            }
        });

        ((ImageView) view.findViewById(R.id.btn_clear_text_search)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchValue.setText("");
            }
        });

        redditRecyclerView = (RecyclerView) view.findViewById(R.id.redit_recycler_view);
        redditCallback = redCallback();
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        redditRecyclerView.setLayoutManager(layoutManager);
        displayPostsInRecyclerView(redditPostingList);
    }

    RedditRecyclerAdapter.Callback recAdapterCallback = null;

    // implements RedditRecyclerAdapter.Callback
    RedditRecyclerAdapter.Callback getRedditRecyclerAdapterCallback(){
        if (recAdapterCallback==null) {
            recAdapterCallback = new RedditRecyclerAdapter.Callback() {

                @Override
                public void getAfter() {
                    if (!searching) {
//                        after = "after=" + listing.getData().getAfter();
                        after = listing.getData().getAfter();
                        doSearch(searchValue.getText().toString(), after);
                    }
                }
            };
        }
        return recAdapterCallback;
    }

    private void doSearch(String subreddit, String extra) {

        searching = true;

        Log.d(TAG, "search param{" + subreddit.toString() + ", " + (extra == null ? "" : extra) + "}");

        // clear previous list if a new search (extra == null)
        if (extra == null) {
            redditPostingList.clear();
            //
            // @GET("/r/{subreddit}.json")
            // void getSubreddit(@Path("subreddit") String subreddit,
            //         Callback<RedditResponse<RedditListing>> callback)
            //
            RedditService.Implementation.get().getSubreddit(subreddit, redditCallback);
        } else {
            RedditService.Implementation.get().getSubredditAfter(subreddit, extra, redditCallback);
        }

    }

    // implements RedditRecyclerAdapter.Callback
    @NonNull
    private Callback<RedditResponse<RedditListing>> redCallback() {
        return new Callback<RedditResponse<RedditListing>>() {
            @Override
            public void success(RedditResponse<RedditListing> listing, Response response) {
//                if (isDestroyed()) {
//                    return;
//                }
                //                        mProgressDialog.dismiss();
                Log.d(TAG, "response url:"+response.getUrl());
                Log.d(TAG, "response status:"+response.getStatus());
                onListingReceived(listing);
                searching = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "RetrofitError URL :" + error.getUrl());
                Log.e(TAG, "RetrofitError detailMessage :"+error.getMessage());
//                if (isDestroyed()) {
//                    return;
//                }
                //                        mProgressDialog.dismiss();
                new AlertDialog.Builder(activity)
                        .setMessage("Loading failed :(")
                        .setCancelable(false)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                after = null;
                            }
                        })
                        .show();
            }
        };
    }

    private void onListingReceived(RedditResponse<RedditListing> listing) {
        this.listing = listing;
        Log.d(TAG, "after :"+listing.getData().getAfter());
        ListIterator<RedditObject> itr = listing.getData().getChildren().listIterator();
        while (itr.hasNext()) {
            RedditObject rObject = itr.next();
            RedditLink rLink = (RedditLink) rObject;
            redditPostingList.add(rLink);
        }
        Log.i(TAG, "redditPostingList size:"+redditPostingList.size());
        redditRecyclerAdapter.notifyDataSetChanged();
    }


    /**
     * Display RedditModel postings in RecyclerView
     *
     * @param redditLinkList
     */
    private void displayPostsInRecyclerView(ArrayList<RedditLink> redditLinkList) {

        redditRecyclerView = (RecyclerView) view.findViewById(R.id.redit_recycler_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(activity);
        redditRecyclerView.setLayoutManager(layoutManager);

        redditRecyclerAdapter = new RedditRecyclerAdapter(getActivity(), getRedditRecyclerAdapterCallback(), redditLinkList);

        redditRecyclerView.setAdapter(redditRecyclerAdapter);

    }

    @Override
    public void onAttach(Context context) {
        this.activity = this.getActivity();
        Log.i(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnRedditSearchFragmentListener {
        // TODO: Update argument type and name
        public void onSelectedItem(int dummy);
    }

}
