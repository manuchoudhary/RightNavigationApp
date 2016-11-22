package com.example.manishchoudhary.rightnavigationapp;

import android.content.Context;

import io.realm.RealmResults;

/**
 * Created by manish.choudhary on 11/20/2016.
 */

public class RealmFeedItemAdapter extends RealmModelAdapter<FeedItem> {

    public RealmFeedItemAdapter(Context context, RealmResults<FeedItem> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
