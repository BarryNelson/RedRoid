package com.nelsoft.redroid;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nelsoft.redroid.reddit.model.RedditLink;
import com.nelsoft.redroid.reddit_search.RedditSearchFragment;

public class MainActivity
        extends AppCompatActivity
        implements
        RedditSearchFragment.OnRedditSearchFragmentListener
        , DetailFragment.OnFragmentInteractionListener {
    
    private static final String TAG = "MainActivity";
    private DetailFragment detailFragment;
    private RedditSearchFragment redditSearchFragment;
    private FragmentTransaction transaction;

    @Override
    public void onBackPressed() {
        if (detailFragment == null) {
            super.onBackPressed();
        } else {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.detach(detailFragment);
            transaction.commit();
            detailFragment = null;
        }
    }

    /**
     * @param outState
     * @see android.support.v4.app.FragmentActivity
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    /**
     * api 23
     *
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.i(TAG, "SAVEREST onSaveInstanceState API(23)");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "SAVEREST onCreate savedInstanceState:" + savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            launchSearchFragment();
        }
    }

    private void launchSearchFragment() {
        Log.i(TAG, "launchSearchFragment:");
        redditSearchFragment = new RedditSearchFragment();

        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragHolder, redditSearchFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onSelectedItem(RedditLink lineLink) {
        Log.i(TAG, "onSelectedItem:" + lineLink.getUrl());
        detailFragment = DetailFragment.newInstance(lineLink.getUrl(), lineLink.getAuthor(), lineLink.getTitle());

        transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragDetailHolder, detailFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
