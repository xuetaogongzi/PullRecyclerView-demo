package com.wangjw.pullrecyclerview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wangjw.pullrecyclerviewlib.PullRecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout mRefreshLayout;
    private PullRecyclerView mRecyclerView;

    private MyAdapter mAdapter;
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.SwipeRefreshLayout);
        mRecyclerView = (PullRecyclerView) findViewById(R.id.RecyclerView);

        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        loadData();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIndex = 0;
                mAdapter.resetCount();
                mRecyclerView.resetLoadingState();
                loadData();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new PullRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData();
            }
        });
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Load data" + (mIndex + 1));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addData();
                        mIndex++;

                        if (mIndex == 1) {
                            mRefreshLayout.setRefreshing(false);
                        }

                        if (mIndex == 2) {
                            mRecyclerView.showLoadingError();
                            return;
                        }

                        if (mIndex < 5) {
                            mRecyclerView.loadMoreSuccess();
                        } else {
                            mRecyclerView.showLoadingComplete();
                        }
                    }
                });
            }
        }).start();
    }

}
