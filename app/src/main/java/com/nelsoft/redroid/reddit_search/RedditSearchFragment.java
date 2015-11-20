package com.nelsoft.redroid.reddit_search;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

    public static final String KEY_FOR_LAYOUT_MANAGER_STATE = "redditsearchfragment.recycler.layout";

    private static final String TAG = "RedditSearchFragment";

    ArrayList<RedditLink> redditPostingList = new ArrayList<>();
    RedditRecyclerAdapter.Callback recAdapterCallback = null;
    private View view;
    private Context context;
    private Callback<RedditResponse<RedditListing>> redditCallback;
    private OnRedditSearchFragmentListener callback;
    private EditText searchValue;
    private String after = null;
    /**
     * prevent multiple calls for more data
     */
    private boolean searching = false;
    private RecyclerView redditRecyclerView;
    private RedditRecyclerAdapter redditRecyclerAdapter;
    private LinearLayoutManager layoutManager;
    private RedditResponse<RedditListing> listing;
    private ProgressDialog mProgressDialog;
    private boolean restored = false;
    private boolean cleared = false;
    
    public RedditSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_reddit_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initControls();
        if (!restored) {
            doSearch(searchValue.getText().toString(), null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "SAVEREST onSaveInstanceState(Bundle outState)");
        Log.d(TAG, "SAVEREST location :" + layoutManager.findFirstCompletelyVisibleItemPosition());
        outState.putParcelable(KEY_FOR_LAYOUT_MANAGER_STATE, layoutManager.onSaveInstanceState());
        outState.putString("SEARCH", searchValue.getText().toString());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "SAVEREST onViewStateRestored :" + savedInstanceState);
        if (savedInstanceState != null) {
            restored = true;
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_FOR_LAYOUT_MANAGER_STATE);
            redditRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void initControls() {

        searchValue = (EditText) view.findViewById(R.id.text_search);
//        searchValue.setInputType(InputType.TYPE_NULL);
//        searchValue.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                searchValue.setInputType(InputType.TYPE_CLASS_TEXT);
//                searchValue.onTouchEvent(event); // call native handler
//                return true; // consume touch event
//            }
//
//
//        });
        //        searchValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
        //                Log.d(TAG, "onFocusChange " + (v.isFocused() ? "focused" : "fuzzy"));
        //                if (!cleared && searchValue.isFocused()) {
        //                    cleared = true;
        //                    searchValue.clearFocus();
        //                }
//            }
//        });

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

    // implements RedditRecyclerAdapter.Callback

    /**
     * RedditRecyclerAdapter.Callback
     *
     * @return edditRecyclerAdapter.Callback
     * @see RedditRecyclerAdapter.Callback#getMore()
     * @see RedditRecyclerAdapter.Callback#onSelectedItem(RedditLink)
     */
    RedditRecyclerAdapter.Callback getRedditRecyclerAdapterCallback() {
        if (recAdapterCallback == null) {
            recAdapterCallback = new RedditRecyclerAdapter.Callback() {

                /**
                 * do another search on subreddit.getData().getMore()
                 */
                @Override
                public void getMore() {
                    if (!searching) {
                        after = listing.getData().getMore();
                        doSearch(searchValue.getText().toString(), after);
                    }
                }

                /**
                 * @see com.nelsoft.redroid.reddit_search.RedditRecyclerAdapter.Callback
                 * @param lineLink
                 */
                @Override
                public void onSelectedItem(RedditLink lineLink) {
                    callback.onSelectedItem(lineLink);
                }
            };
        }
        return recAdapterCallback;
    }

    private void doSearch(String subreddit, String extra) {

        searching = true;

        Log.d(TAG, "search param{" + subreddit + ", " + (extra == null ? "" : extra) + "}");

        // clear previous list if a new search (extra == null)
        if (extra == null) {
            redditPostingList.clear();
            redditRecyclerAdapter.notifyDataSetChanged();

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading subreddit " + subreddit);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            RedditService.Implementation.get().getSubreddit(subreddit, redditCallback);
        } else {
            RedditService.Implementation.get().getSubredditAfter(subreddit, extra, redditCallback);
        }

        hideKeyboard();
    }

    private void hideKeyboard() {
        //  if (searchValue.isFocused()){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchValue.getWindowToken(), 0);
        // }
    }

    // implements RedditRecyclerAdapter.Callback
    @NonNull
    private Callback<RedditResponse<RedditListing>> redCallback() {
        return new Callback<RedditResponse<RedditListing>>() {

            @Override
            public void success(RedditResponse<RedditListing> listing, Response response) {
                Log.d(TAG, "response url:" + response.getUrl() + ", status:" + response.getStatus());
                onListingReceived(listing);
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                searching = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "RetrofitError URL :" + error.getUrl());
                Log.e(TAG, "RetrofitError detailMessage :" + error.getMessage());
                if (getActivity().isDestroyed()) {
                    return;
                }
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                new AlertDialog.Builder(context)
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
        ListIterator<RedditObject> itr = listing.getData().getChildren().listIterator();
        while (itr.hasNext()) {
            redditPostingList.add((RedditLink) itr.next());
        }
        Log.i(TAG, "added RedditLink items, redditPostingList size:" + redditPostingList.size());
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
        layoutManager = new LinearLayoutManager(context);
        redditRecyclerView.setLayoutManager(layoutManager);

        redditRecyclerAdapter = new RedditRecyclerAdapter(getActivity(), getRedditRecyclerAdapterCallback(), redditLinkList);

        redditRecyclerView.setAdapter(redditRecyclerAdapter);

    }

    /**
     * for API below 23
     *
     * @param activity
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "onAttach(Activity activity)");
        super.onAttach(activity);
        this.context = activity;

        try {
            callback = (OnRedditSearchFragmentListener) activity;
        } catch (Exception e) {
            throw new ClassCastException(activity.toString() + " must implement OnRedditSearchFragmentListener");
        }
    }

    /**
     * for API 23+
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach(Context context)");
        super.onAttach(context);
        this.context = context;

        try {
            callback = (OnRedditSearchFragmentListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must implement OnRedditSearchFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach()");
        super.onDetach();
        callback = null;
        redditCallback = null;
    }

    /**
     * RedditSearchFragment.OnRedditSearchFragmentListener
     *
     * @see OnRedditSearchFragmentListener#onSelectedItem(RedditLink)
     */
    public interface OnRedditSearchFragmentListener {

        /**
         * Receives a selected RedditLink item
         *
         * @param redditLink - the seleced RecyclerList item
         */
        public void onSelectedItem(RedditLink redditLink);
    }

}
