package com.nelsoft.redroid;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nelsoft.redroid.reddit_search.RedditRecyclerAdapter;
import com.nelsoft.redroid.reddit_search.RedditSearchFragment;

public class MainActivity
        extends AppCompatActivity
        implements
        RedditSearchFragment.OnRedditSearchFragmentListener
        , RedditRecyclerAdapter.Callback{
    
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchSearchFragment();
    }

    private void launchSearchFragment() {

        RedditSearchFragment redditSearchFragment = new RedditSearchFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragHolder, redditSearchFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    @Override
    public void onSelectedItem(int dummy) {
        Log.i(TAG, "selected :"+dummy);
    }

    // RedditRecyclerAdapter.Callback
    @Override
    public void getAfter() {
        Log.i(TAG, "getAfter :");
    }
}
