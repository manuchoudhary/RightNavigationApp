package com.example.manishchoudhary.rightnavigationapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by manish.choudhary on 11/17/2016.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView listView;
    private FeedListAdapter listAdapter;
    private Realm realm;
    private ArrayList<FeedItem> feedItems;
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.realm = RealmController.with(this).getRealm();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (RecyclerView) findViewById(R.id.list);

        setupRecycler();

        if (!Prefs.with(this).getPreLoad()) {
            setRealmData();
        }
        else{
            List<FeedItem> itemList = new ArrayList<FeedItem>();
            itemList = RealmController.with(this).getFeedItems();
            listAdapter = new FeedListAdapter(this, itemList);
            listView.setAdapter(listAdapter);
            RealmController.with(this).refresh();
            setRealmAdapter(RealmController.with(this).getFeedItems());
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView leftNavigationView = (NavigationView) findViewById(R.id.nav_left_view);
        leftNavigationView.setNavigationItemSelectedListener(this);

        NavigationView rightNavigationView = (NavigationView) findViewById(R.id.nav_right_view);
        rightNavigationView.setNavigationItemSelectedListener(this);
    }

    public void setRealmAdapter(RealmResults<FeedItem> itemList) {

        RealmFeedItemAdapter realmAdapter = new RealmFeedItemAdapter(this.getApplicationContext(), itemList, true);
        // Set the data and tell the RecyclerView to draw
        listAdapter.setRealmAdapter(realmAdapter);
        listAdapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        listView.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
    }

    private void setRealmData() {

        StringRequest stringRequest = new StringRequest(URL_FEED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            parseJsonFeed(new JSONObject(response));
                            RealmController.with(MainActivity.this).refresh();
                            setRealmAdapter(RealmController.with(MainActivity.this).getFeedItems());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");
            feedItems = new ArrayList<FeedItem>();
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();

                switch(feedObj.getInt("id")){
                    case 8:
                        if(count == 1){
                            item.setId(12);
                        }
                        if(count == 0){
                            item.setId(feedObj.getInt("id"));
                            count = 1;
                        }
                        break;
                    default:
                        item.setId(feedObj.getInt("id"));
                        break;
                }

                item.setName(feedObj.getString("name"));

                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImage(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);
                item.setIsLiked(false);

                feedItems.add(item);
            }
            for (FeedItem item : feedItems) {
                // Persist your data easily
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(item);
                realm.commitTransaction();
            }
            Prefs.with(this).setPreLoad(true);

            listAdapter = new FeedListAdapter(this, feedItems);
            listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {  /*Closes the Appropriate Drawer*/
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_openRight) {
            drawer.openDrawer(GravityCompat.END);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.nav_left_view){
            int id = item.getItemId();
            if (id == R.id.nav_camera) {
            } else if (id == R.id.nav_gallery) {
            } else if (id == R.id.nav_slideshow) {
            } else if (id == R.id.nav_manage) {
            } else if (id == R.id.nav_share) {
            } else if (id == R.id.nav_send) {
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        if(item.getItemId() == R.id.nav_right_view){
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
            } else if (id == R.id.nav_logout) {
            } else if (id == R.id.nav_help) {
            } else if (id == R.id.nav_about) {
                Toast.makeText(MainActivity.this, "Right Drawer - About", Toast.LENGTH_SHORT).show();
            }

            drawer.closeDrawer(GravityCompat.END); /*Important Line*/
            return true;
        }
        return false;
    }
}
