package com.example.manishchoudhary.rightnavigationapp;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by manish.choudhary on 11/20/2016.
 */

public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(FeedItem.class);
        realm.commitTransaction();
    }

    public RealmResults<FeedItem> getFeedItems() {

        return realm.where(FeedItem.class).findAll();
    }

    //query a single item with the given id
    public FeedItem getFeedItem(int id) {
        return realm.where(FeedItem.class).equalTo("id", id).findFirst();
    }

    public boolean hasFeedItems() {

        return !realm.allObjects(FeedItem.class).isEmpty();
    }

    //query example
    public RealmResults<FeedItem> queryedBooks() {

        return realm.where(FeedItem.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();

    }
}

