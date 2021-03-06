package com.nelsoft.redroid;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_URL = "URL";
    private static final String ARG_AUTHOR = "AUTHOR";
    private static final String ARG_TITLE = "TITLE";

    // TODO: Rename and change types of parameters
    private String strURL;
    private String author;
    private String title;

    private OnFragmentInteractionListener mListener;
    private WebView detailImage;
    private TextView detailTitle;
    private TextView detailAuthor;
    private Context context;
    
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param strURL Parameter 1.
     * @param author Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String strURL, String author, String title) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, strURL);
        args.putString(ARG_AUTHOR, author);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }
    
    public DetailFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strURL = getArguments().getString(ARG_URL);
            author = getArguments().getString(ARG_AUTHOR);
            title = getArguments().getString(ARG_TITLE);
        }
        strURL = RedditBleach.getInstance().findGraphicObject(strURL);
    }

    private void display() {
        detailTitle.setText(title);
        detailAuthor.setText(author);
//        Glide.with(context).load(strURL).into(detailImage);
        detailImage.getSettings().setJavaScriptEnabled(true);
        detailImage.setWebViewClient(new WebViewClient());
        detailImage.getSettings().setDomStorageEnabled(true); // localStorage
        detailImage.setWebChromeClient(new WebChromeClient()); // for alerts to appear
//        detailImage.setInitialScale(30);
        detailImage.getSettings().setLoadWithOverviewMode(true);
        detailImage.getSettings().setUseWideViewPort(true);
        if (strURL.contains("gif")) {
            detailImage.setInitialScale(100);
        }
//        detailImage.invokeZoomPicker();
        detailImage.loadUrl(strURL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
//        detailImage = (ImageView) view.findViewById(R.id.detailImage);
        detailImage = (WebView) view.findViewById(R.id.detailImage);
        detailTitle = (TextView) view.findViewById(R.id.detailTitle);
        detailAuthor = (TextView) view.findViewById(R.id.detailAuthor);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        display();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * for API below 23
     * @param activity
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    /**
     * for API 23+
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    
}
