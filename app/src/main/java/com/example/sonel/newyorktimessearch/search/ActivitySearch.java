package com.example.sonel.newyorktimessearch.search;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sonel.newyorktimessearch.Modele.Article;
import com.example.sonel.newyorktimessearch.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by sonel on 7/26/2017.
 */

public class ActivitySearch extends AppCompatActivity {


    public final static String NYTIMES_URL =
            "https://api.nytimes.com/svc/search/v2/articlesearch.json?";
    public final static String API_KEY = "api-key";
    public final static String PAGE = "page";
    public final static String NY_BEGIN_DATE = "begin_date";
    public final static String NY_NEWS_DESK = "fq";
    public final static String NY_SORT_ORDER = "sort";
    public final static String QUERY = "q";
    public final static String RESPONSE = "response";
    public final static String DOCS = "docs";
    public final static String WEB_URL = "web_url";
    private final static String KEY = "7c5d0b20be914549b3bc6e3e008407f4";

    public static String cachedQueryString = "new york times";

    ArrayList<Article> articles;
    RecyclerViewArticleAdapter mRecyclerViewArticleAdapter;

    public enum SortOrder {
        NEWEST("newest"), OLDEST("oldest");

        private String sortOrder;
        SortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }
        public String getSortOrder() {
            return sortOrder;
        }
    }

    public enum NewsDesk {
        ARTS("Arts"), FASHION_AND_STYLE("Fashion & Style"), SPORTS("Sports");

        private String newsDesk;
        NewsDesk(String newsDesk) {
            this.newsDesk = newsDesk;
        }

        public String getNewsDesk() { return newsDesk; }
    }

    public static class FilterAttributes {
        //YYYYMMDD
        static String beginDate =  "";
        static String beginDateDisplay =  "";
        static SortOrder sortOrder = SortOrder.NEWEST;
        static List<NewsDesk> newsDesks = new ArrayList<>();
    }

    @Bind(R.id.rvArticles)
    RecyclerView articlesRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("Search Article");

        articles = new ArrayList<>();
        mRecyclerViewArticleAdapter = new RecyclerViewArticleAdapter(this, articles);

        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        articlesRecyclerView.addItemDecoration(new DividerItemDecoration(this, gridLayoutManager.VERTICAL));
        articlesRecyclerView.setItemAnimator(new DefaultItemAnimator());



        articlesRecyclerView.addOnScrollListener(
                new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        customLoadMoreDataFromApi(page);
                    }
                });


//        articlesRecyclerView.addItemDecoration(
//                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        articlesRecyclerView.setAdapter(mRecyclerViewArticleAdapter);

        // Set layout manager to position the items
        // Attach the layout manager to the recycler view
        articlesRecyclerView.setLayoutManager(gridLayoutManager);

        if (!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Network connectivity problem",
                    Toast.LENGTH_LONG).show();
            //TODO launch activity and show failure droid
        } else if (!isOnline()) {
            Toast.makeText(getApplicationContext(), "Your device is not online, " +
                            "check wifi and try again!",
                    Toast.LENGTH_LONG).show();
        } else {
            searchArticle(cachedQueryString, 0);
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration{
//                new DividerItemDecoration(t {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    //TODO implement this
    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void customLoadMoreDataFromApi(int offset) {
        offset = offset % 100;
        Toast.makeText(this, "Loading more...", Toast.LENGTH_SHORT).show();
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        // For efficiency purposes, notify the adapter of only the elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        searchArticle(cachedQueryString, offset);
        int curSize = mRecyclerViewArticleAdapter.getItemCount();
        mRecyclerViewArticleAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);
    }

    public void searchArticle(String searchText, int page) {
        Log.i("SearchActivity", "Search text is " + searchText);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = constructQueryRequestParams(searchText, page);
        asyncHttpClient.get(NYTIMES_URL, requestParams, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {

                try {
                    if (response != null) {
                        Gson gson = new GsonBuilder().create();
                        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
                        if (jsonObject.has(RESPONSE)) {
                            JsonObject jsonResponseObject = jsonObject.getAsJsonObject(RESPONSE);
                            if (jsonResponseObject != null) {
                                JsonArray jsonDocsArray = jsonResponseObject.getAsJsonArray(DOCS);
                                Type collectionType = new TypeToken<List<Article>>() {
                                }.getType();

                                List<Article> fetchedArticles = gson.fromJson(jsonDocsArray,
                                        collectionType);
                                articles.addAll(fetchedArticles);
                                Log.i("SearchActivity", articles.size() + " articles found");
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    Log.w("AsyncHttpClient", "Exception while parsing json " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Opps looks like " +
                                    "some problem, try searching again",
                            Toast.LENGTH_SHORT).show();

                }
                mRecyclerViewArticleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response,
                                  Throwable throwable) {
                Log.w("AsyncHttpClient", "HTTP Request failure: " + statusCode + " " +
                        throwable.getMessage());

                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "Opps looks like " +
                                    "network connectivity problem",
                            Toast.LENGTH_LONG).show();
                }

                if (!isOnline()) {
                    Toast.makeText(getApplicationContext(), "Your device is not online, " +
                                    "check wifi and try again!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void reSearch() {
        articles.clear();
        mRecyclerViewArticleAdapter.notifyDataSetChanged();
        searchArticle(cachedQueryString, 0);
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                articles.clear();
                mRecyclerViewArticleAdapter.notifyDataSetChanged();
                cachedQueryString = query;
                searchArticle(query, 0);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//Search Filter Articles
        int id = item.getItemId();
        if (id == R.id.action_search_filter) {
            launchFilterDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    // FIXME: 7/27/2017 load filter Search Article
  public void launchFilterDialog() {
        SearchFilterFragment searchFilterDialog = new SearchFilterFragment();
        FragmentManager fm = getSupportFragmentManager();
        searchFilterDialog.show(fm, "filter");
    }

    public RequestParams constructQueryRequestParams(String searchText, int pageNumber) {
        RequestParams requestParams = new RequestParams();
        requestParams.put(API_KEY, KEY);

        if (!TextUtils.isEmpty(FilterAttributes.beginDate)) {
            requestParams.put(NY_BEGIN_DATE, FilterAttributes.beginDate);
        }

        if (FilterAttributes.newsDesks.size() > 0) {
            StringBuilder newsDeskBuilder = new StringBuilder();
            newsDeskBuilder.append("news_desk:(");

            if (FilterAttributes.newsDesks.size() > 1) {
                for (int i = 0; i < FilterAttributes.newsDesks.size() - 1; i++) {
                    newsDeskBuilder.append(FilterAttributes.newsDesks.get(i).getNewsDesk());
                    newsDeskBuilder.append(" ");
                }
                newsDeskBuilder.append(FilterAttributes.newsDesks
                        .get(FilterAttributes.newsDesks.size() - 1).getNewsDesk());
            } else {
                newsDeskBuilder.append(FilterAttributes.newsDesks.get(0).getNewsDesk());
            }

            newsDeskBuilder.append(")");
            requestParams.put(NY_NEWS_DESK, newsDeskBuilder.toString());
        }


        requestParams.put(NY_SORT_ORDER, FilterAttributes.sortOrder.getSortOrder());
        requestParams.put(PAGE, pageNumber);
        requestParams.put(QUERY, searchText);

        return requestParams;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (InterruptedException | IOException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("News Article Search");
    }



}
