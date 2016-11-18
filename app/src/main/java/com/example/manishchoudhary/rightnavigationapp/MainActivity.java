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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manish.choudhary on 11/17/2016.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
    private ProgressDialog progress;
    Cache.Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (RecyclerView) findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this));

        feedItems = new ArrayList<FeedItem>();

        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        entry = cache.get(URL_FEED);
        startProgress();

        Progress pro = new Progress();
        pro.execute();

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

    private void startProgress(){

    }

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                feedItems.add(item);
            }

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
                Toast.makeText(MainActivity.this, "Left Drawer - Import", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_gallery) {
                Toast.makeText(MainActivity.this, "Left Drawer - Gallery", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_slideshow) {
                Toast.makeText(MainActivity.this, "Left Drawer - Slideshow", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_manage) {
                Toast.makeText(MainActivity.this, "Left Drawer - Tools", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_share) {
                Toast.makeText(MainActivity.this, "Left Drawer - Share", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_send) {
                Toast.makeText(MainActivity.this, "Left Drawer - Send", Toast.LENGTH_SHORT).show();
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        if(item.getItemId() == R.id.nav_right_view){
            int id = item.getItemId();
            if (id == R.id.nav_settings) {
                Toast.makeText(MainActivity.this, "Right Drawer - Settings", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_logout) {
                Toast.makeText(MainActivity.this, "Right Drawer - Logout", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_help) {
                Toast.makeText(MainActivity.this, "Right Drawer - Help", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_about) {
                Toast.makeText(MainActivity.this, "Right Drawer - About", Toast.LENGTH_SHORT).show();
            }

            drawer.closeDrawer(GravityCompat.END); /*Important Line*/
            return true;
        }
        return false;
    }

    public class Progress extends AsyncTask<String, Void, Void> {

        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this, R.string.progress_msg);
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            if (entry != null) {
                try {
                    String data = new String(entry.data, "UTF-8");
                    try {
                        parseJsonFeed(new JSONObject(data));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } else {
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                        URL_FEED, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(TAG, "Response: " + response.toString());
                        if (response != null) {
                            parseJsonFeed(response);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });

                AppController.getInstance().addToRequestQueue(jsonReq);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            dialog.dismiss();
        }

    }
}
