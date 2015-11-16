package com.nelsoft.redroid;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nelsoft.redroid.reddit.model.RedditLink;
import com.nelsoft.redroid.reddit_search.RedditSearchFragment;

public class MainActivity
        extends AppCompatActivity
        implements
        RedditSearchFragment.OnRedditSearchFragmentListener
        , DetailFragment.OnFragmentInteractionListener
{
    
    private static final String TAG = "MainActivity";
    private DetailFragment detailFragment;

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
        }
    }

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
    public void onSelectedItem(RedditLink lineLink) {
        Log.i(TAG, "onSelectedItem:" + lineLink.getUrl());
        detailFragment = DetailFragment.newInstance(lineLink.getUrl(),lineLink.getAuthor(),lineLink.getTitle());

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

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
