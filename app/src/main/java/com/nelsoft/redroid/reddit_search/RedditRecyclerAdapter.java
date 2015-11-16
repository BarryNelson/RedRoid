package com.nelsoft.redroid.reddit_search;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nelsoft.redroid.R;
import com.nelsoft.redroid.reddit.model.RedditLink;

import java.util.ArrayList;
import java.util.List;

public class RedditRecyclerAdapter
        extends RecyclerView.Adapter<RedditRecyclerAdapter.ViewHolder>
{

    private final Callback callback;
    private final Context context;
    private String TAG = "RedditRecyclerAdapter";

    private List<RedditLink> redditPostList = new ArrayList<RedditLink>();
    private Fragment fragActivity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RedditRecyclerAdapter(Context context, Callback callback, List<RedditLink> redditPosts) {
        this.context = context;
        this.callback = callback;
        redditPostList = redditPosts;
    }

//    	public void add(int position, String item) {
//    		redditPostList.add(position, item);
//    		notifyItemInserted(position);
//    	}

    public void remove(String item) {
        int position = redditPostList.indexOf(item);
        redditPostList.remove(position);
        notifyItemRemoved(position);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final RedditLink redditLink = redditPostList.get(position);

        // thumbnail
        String thumbnailURL= null;
        if (redditLink.getThumbnail().equals("nsfw")) {
            thumbnailURL = redditLink.getUrl();
            if (thumbnailURL.startsWith("http//imgur.com/")) {
                StringBuffer temp = new StringBuffer(thumbnailURL);
                temp.append(".jpg");
                temp.insert(7, "i.");
                thumbnailURL = temp.toString();
                Log.i(TAG, ">>> " + redditLink.getAuthor() + ">>>> " + thumbnailURL);
            } else {
                Log.i(TAG, "[[ "+thumbnailURL+" ]]");
            }
        } else {
            thumbnailURL = redditLink.getThumbnail();
        }
        Glide.with(context)
                .load(thumbnailURL)
                .into(holder.thumbnail);

        holder.lineAuthor.setText(redditPostList.get(position).getAuthor());
        holder.lineTitle.setText(redditPostList.get(position).getTitle());
        holder.lineLink = redditPostList.get(position);

    }

    /**
     * itme is attached, may not be fully visible until scrolled completely into view
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        Log.i(TAG, "AT>>" + holder.getAdapterPosition() + " : " + holder.getLayoutPosition());
        if(getItemCount() - holder.getAdapterPosition() < 20 ){
//            Log.i(TAG, "Get more!");
            callback.getMore();
        }
        super.onViewAttachedToWindow(holder);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return redditPostList.size();
    }

    // Create new views (invoked by the layout manager)

    @Override
    public RedditRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reddit_line_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView thumbnail;
        private final TextView lineAuthor;
        private final TextView lineTitle;
        protected RedditLink lineLink = null;

        public ViewHolder(View v) {
            super(v);
            lineAuthor = (TextView) v.findViewById(R.id.lineAuthor);
            lineTitle = (TextView) v.findViewById(R.id.lineTitle);
            thumbnail = (ImageView) v.findViewById(R.id.lineThumbnail);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "--> onClick: " + lineLink.getUrl());
            Log.i(TAG, "--> onClick: " + lineLink.getUrl());
            callback.onSelectedItem(lineLink);
        }

        /**
         * returns link to lineitem in RecyclerView.ViewHolder
         * @return
         */
        public RedditLink getLineLink() {
            return lineLink;
        }
    }

    public interface Callback {
        public void getMore();
        public void onSelectedItem(RedditLink lineLink);
    }

}