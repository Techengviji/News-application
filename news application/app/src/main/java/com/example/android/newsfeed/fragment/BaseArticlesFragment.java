/*
 * MIT License
 *
 * Copyright (c) 2018 Soojeong Shin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.android.newsfeed.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.newsfeed.EmptyRecyclerView;
import com.example.android.newsfeed.News;
import com.example.android.newsfeed.NewsLoader;
import com.example.android.newsfeed.NewsPreferences;
import com.example.android.newsfeed.R;
import com.example.android.newsfeed.adapter.NewsAdapter;
import com.example.android.newsfeed.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class BaseArticlesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<News>>{

    private static final String LOG_TAG = BaseArticlesFragment.class.getName();

    private static final int NEWS_LOADER_ID = 1;

    private NewsAdapter mAdapter;

    private TextView mEmptyStateTextView;

    private View mLoadingIndicator;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        EmptyRecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(layoutManager);


        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.swipe_color_1),
                getResources().getColor(R.color.swipe_color_2),
                getResources().getColor(R.color.swipe_color_3),
                getResources().getColor(R.color.swipe_color_4));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                initiateRefresh();
                Toast.makeText(getActivity(), getString(R.string.updated_just_now),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mLoadingIndicator = rootView.findViewById(R.id.loading_indicator);

        mEmptyStateTextView = rootView.findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(mEmptyStateTextView);

        mAdapter = new NewsAdapter(getActivity(), new ArrayList<News>());

        mRecyclerView.setAdapter(mAdapter);

        initializeLoader(isConnected());

        return rootView;
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        Uri.Builder uriBuilder = NewsPreferences.getPreferredUri(getContext());

        Log.e(LOG_TAG,uriBuilder.toString());

        return new NewsLoader(getActivity(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> newsData) {

        mLoadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_news);

        mAdapter.clearAll();

        if (newsData != null && !newsData.isEmpty()) {
            mAdapter.addAll(newsData);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        mAdapter.clearAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        restartLoader(isConnected());
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    private void initializeLoader(boolean isConnected) {
        if (isConnected) {

            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {

            mLoadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateTextView.setCompoundDrawablesWithIntrinsicBounds(Constants.DEFAULT_NUMBER,
                    R.drawable.ic_network_check,Constants.DEFAULT_NUMBER,Constants.DEFAULT_NUMBER);
        }
    }


    private void restartLoader(boolean isConnected) {
        if (isConnected) {

            LoaderManager loaderManager = getLoaderManager();

            loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
        } else {

            mLoadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateTextView.setCompoundDrawablesWithIntrinsicBounds(Constants.DEFAULT_NUMBER,
                    R.drawable.ic_network_check,Constants.DEFAULT_NUMBER,Constants.DEFAULT_NUMBER);

            mSwipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    private void initiateRefresh() {
        restartLoader(isConnected());
    }
}
