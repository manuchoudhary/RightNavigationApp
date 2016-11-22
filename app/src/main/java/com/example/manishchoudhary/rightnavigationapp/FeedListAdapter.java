package com.example.manishchoudhary.rightnavigationapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by manish.choudhary on 11/17/2016.
 */

public class FeedListAdapter extends RealmRecyclerViewAdapter<FeedItem> {

    public static final int TEXTVIEW = 0;
    public static final int IMAGEVIEW = 1;

    private Activity activity;
    private Realm realm;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    private FeedItem feed;
    ImageLoader imageLoader;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
        imageLoader = AppController.getInstance().getImageLoader();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        com.like.LikeButton btnLike;
        public ViewHolder(View v) {
            super(v);
            this.btnLike = (com.like.LikeButton) v.findViewById(R.id.btnLike);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public class TextViewHolder extends ViewHolder {
        TextView temp;
        com.like.LikeButton btnLike;
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
            if (feedItem.getImage() != null && !feedItem.getImage().isEmpty()) {
                result = 1;
            } else {
                result = 0;
            }
        }
        return result;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();
        FeedItem item = getItem(position);


        final ViewHolder cardView = (ViewHolder) viewHolder;
        cardView.btnLike.setLiked(item.getIsLiked());

        if (getItemViewType(position) == TEXTVIEW) {
            TextViewHolder holder = (TextViewHolder) viewHolder;
            if (!feedItems.get(position).getStatus().isEmpty()) {
                holder.temp.setText(feedItems.get(position).getStatus());
            } else {
                holder.temp.setVisibility(View.GONE);
            }
        }
        else if (getItemViewType(position) == IMAGEVIEW) {
            ImageViewHolder holder = (ImageViewHolder) viewHolder;
            if (feedItems.get(position).getImage() != null) {
                holder.feedImg.setImageUrl(feedItems.get(position).getImage() , imageLoader);
                holder.feedImg.setVisibility(View.VISIBLE);
            } else {
                holder.feedImg.setVisibility(View.GONE);
            }
        }
        cardView.btnLike.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                cardView.btnLike.setLikeDrawableRes(R.drawable.thumb_on);
                feedLiked(position);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                cardView.btnLike.setLikeDrawableRes(R.drawable.thumb_off);
                feedUnliked(position);
            }
        });
    }

    public void feedLiked(int position){
        RealmResults<FeedItem> results = realm.where(FeedItem.class).findAll();
        int id = results.get(position).getId();
        String name = realm.where(FeedItem.class).equalTo("id", id).findFirst().getName();
        realm.beginTransaction();
        realm.where(FeedItem.class).equalTo("id", id).findFirst().setIsLiked(true);
        realm.commitTransaction();
        notifyDataSetChanged();
        notification("Like", name);
    }

    public void feedUnliked(int position){
        RealmResults<FeedItem> results = realm.where(FeedItem.class).findAll();
        int id = results.get(position).getId();
        String name = realm.where(FeedItem.class).equalTo("id", id).findFirst().getName();
        realm.beginTransaction();
        realm.where(FeedItem.class).equalTo("id", id).findFirst().setIsLiked(false);
        realm.commitTransaction();
        notifyDataSetChanged();
        notification("Unlike", name);
    }

    public void notification(String like, String name){
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("Like", like);
        notificationIntent.putExtra("Name", name);

        PendingIntent broadcast = PendingIntent.getBroadcast(activity, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }
}

