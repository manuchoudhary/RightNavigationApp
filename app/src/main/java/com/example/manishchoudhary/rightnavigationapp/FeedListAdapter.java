package com.example.manishchoudhary.rightnavigationapp;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by manish.choudhary on 11/17/2016.
 */

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder> {

    public static final int TEXTVIEW = 0;
    public static final int IMAGEVIEW = 1;

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
        imageLoader = AppController.getInstance().getImageLoader();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public class TextViewHolder extends ViewHolder {
        TextView temp;

        public TextViewHolder(View v) {
            super(v);
            this.temp = (TextView) v.findViewById(R.id.txtStatusMsg);
        }
    }

    public class ImageViewHolder extends ViewHolder {
        FeedImageView feedImg;

        public ImageViewHolder(View v) {
            super(v);
            this.feedImg = (FeedImageView) v.findViewById(R.id.feedImage1);
        }
    }

    @Override
    public FeedListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == TEXTVIEW) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_card, parent, false);
            return new TextViewHolder(v);
        }
        else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_card, parent,false);
            return new ImageViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if (position != feedItems.size()) {
            FeedItem feedItem = feedItems.get(position);
            if (feedItem.getImge() != null) {
                result = 1;
            } else {
                result = 0;
            }
        }
        return result;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (getItemViewType(position) == TEXTVIEW) {
            TextViewHolder holder = (TextViewHolder) viewHolder;
            if (!TextUtils.isEmpty(feedItems.get(position).getStatus())) {
                holder.temp.setText(feedItems.get(position).getStatus());
            } else {
                holder.temp.setVisibility(View.GONE);
            }
        }
        else if (getItemViewType(position) == IMAGEVIEW) {
            ImageViewHolder holder = (ImageViewHolder) viewHolder;
            if (feedItems.get(position).getImge() != null) {
                holder.feedImg.setImageUrl(feedItems.get(position).getImge() , imageLoader);
                holder.feedImg.setVisibility(View.VISIBLE);
            } else {
                holder.feedImg.setVisibility(View.GONE);
            }
        }
    }
}

