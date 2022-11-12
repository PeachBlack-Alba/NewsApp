package com.example.android.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener {

    private static final String NEWS_Request_url= "https://content.guardianapis.com/search?"; //"https://content.guardianapis.com/search?q=science&api-key=test&show-fields=thumbnail&page-size=100&order-by=newest";

    private static final int news_LOADER_ID = 1;

    /** Adapter for the list of earthquakes */
    private NewsAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

/** swipe to refresh*/
    private SwipeRefreshLayout mSwipeRefreshLayout;

    ListView newsListView;
    private ShimmerFrameLayout mShimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        newsListView=(ListView) findViewById(R.id.list);

        mShimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer);
        mShimmerFrameLayout.startShimmer();

        mEmptyStateTextView=(TextView)findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        mAdapter=new NewsAdapter(this,new ArrayList<News>());
        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                News currentNews=mAdapter.getItem(position);
                Uri newsuri=Uri.parse(currentNews.getWebUrl());
                Intent intent=new Intent(Intent.ACTION_VIEW,newsuri);
                startActivity(intent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(news_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
           // View loadingIndicator = findViewById(R.id.loading_indicator);
            //loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet);
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        // Create a new loader for the given URL
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

      String  mNewsType = sharedPrefs.getString(getString(R.string.title_news_type_preference_key),
              getString(R.string.default_news_type_value));

      String  mOrderBy = sharedPrefs.getString(getString(R.string.title_order_by_preference_key),
              getString(R.string.default_order_by_value));

       String mNewsLimit = sharedPrefs.getString(getString(R.string.title_news_limit_preference_key),
               getString(R.string.default_news_limit_value));

        newsListView.setVisibility(View.GONE);
        mShimmerFrameLayout.setVisibility(View.VISIBLE);
        Uri baseUri = Uri.parse(NEWS_Request_url);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", mNewsType);
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("page-size", mNewsLimit);
        uriBuilder.appendQueryParameter("order-by", mOrderBy);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> news) {

        mSwipeRefreshLayout.setRefreshing(false);
        mShimmerFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mShimmerFrameLayout.stopShimmer();
                mShimmerFrameLayout.hideShimmer();
                mShimmerFrameLayout.setVisibility(View.GONE);
                newsListView.setVisibility(View.VISIBLE);
            }
        },8000);

        // Hide loading indicator because the data has been loaded
      //  View loadingIndicator = findViewById(R.id.loading_indicator);
     //   loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            // this is for no result found
            mAdapter.addAll(news);
            // display data
            //updateUi(earthquakes);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {

        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(news_LOADER_ID, null, this);
    }
}