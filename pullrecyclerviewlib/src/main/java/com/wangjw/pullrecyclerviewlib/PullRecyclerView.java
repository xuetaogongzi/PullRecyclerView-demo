package com.wangjw.pullrecyclerviewlib;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangjw on 16/11/2.<br>
 *
 *  使用了3个标记位记录控件的状态:<br>
 *  1.是否正在加载更多      <br>
 *  2.是否加载更多失败    <br>
 *  3.是否全部数据加载完毕 <br><br>
 *
 * 使用流程: <br>
 *  1.初始化后, 程序加载第一页数据, 控件默认显示"正在加载数据中"<br>
 *  2.等待第一页的数据结果返回<br>
 *  3.如果数据返回成功, 有下一页, 则调用loadMoreSuccess()方法<br>
 *  4.如果数据返回失败, 则调用showLoadingError()方法<br>
 *  5.如果全部数据返回完毕, 则调用showLoadingComplete()方法<br>
 *  6.如果用户进行下拉刷新, 则调用resetLoadingState()方法<br><br>
 *
 *  注意事项: <br>
 *  1.一页的数据显示在屏幕上的总高度必须大于RecyclerView可显示的高度, 否则列表不能滑动, 因此不能触发加载更多事件<br>
 */
public class PullRecyclerView extends RecyclerView implements View.OnClickListener {

    public static interface OnLoadMoreListener {
        public void onLoadMore();
    }

    private WrappedRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private boolean mIsLoadingMore = true;        //是否正在加载中,默认初始化状态为正在加载数据中
    private boolean mIsLoadingComplete = true;    //是否加载完毕
    private boolean mIsLoadingError = true;       //是否加载错误

    private String mDefLoadingMoreStr;
    private String mDefLoadingCompleteStr;
    private String mDefaultErrStr;

    private OnLoadMoreListener mLoadMoreListener;

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    public PullRecyclerView(Context context) {
        this(context, null);
    }

    public PullRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Resources res = getResources();
        mDefLoadingMoreStr = res.getString(R.string.pr_loading_more);
        mDefaultErrStr = res.getString(R.string.pr_loading_err);
        mDefLoadingCompleteStr = res.getString(R.string.pr_load_complete);

        mLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLayoutManager);

        addOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onClick(View v) {
        if (!mIsLoadingMore && mIsLoadingError && !mIsLoadingComplete && mLoadMoreListener != null) {
            showLoadingMore();
            mLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mAdapter.getWrappedAdapter() != null && mDataObserver != null) {
            mAdapter.getWrappedAdapter().unregisterAdapterDataObserver(mDataObserver);
        }

        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }

        mAdapter = new WrappedRecyclerAdapter(getContext(), adapter);
        mAdapter.setOnErrorRetryClickListener(this);
        super.setAdapter(mAdapter);

        resetLoadingState();
        showLoadingMore();
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        if (mAdapter != null && mAdapter.getWrappedAdapter() != null && mDataObserver != null) {
            mAdapter.getWrappedAdapter().unregisterAdapterDataObserver(mDataObserver);
        }

        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }

        mAdapter = new WrappedRecyclerAdapter(getContext(), adapter);
        mAdapter.setOnErrorRetryClickListener(this);
        super.swapAdapter(mAdapter, removeAndRecycleExistingViews);

        resetLoadingState();
        showLoadingMore();
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    public Adapter getWrappedAdapter() {
        return mAdapter.getWrappedAdapter();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mLoadMoreListener = onLoadMoreListener;
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (canLoadingMore(dy)) {
                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mAdapter.getItemCount();

                if (lastVisibleItem == totalItemCount - 2) {
                    //修改状态为正在加载
                    showLoadingMore();
                    mLoadMoreListener.onLoadMore();
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    /**
     * 判断是否能加载更多
     * @param dy
     * @return
     */
    private boolean canLoadingMore(int dy) {
        if (!mIsLoadingComplete && !mIsLoadingMore && !mIsLoadingError && dy > 0 && mLoadMoreListener != null) {
            return true;
        }
        return false;
    }

    private void showLoadingMore() {
        showLoadingMore(mDefLoadingMoreStr);
    }

    private void showLoadingMore(int strResId) {
        showLoadingMore(getResources().getString(strResId));
    }

    /**
     * 显示加载更多,调用此方法后,表示系统正在加载数据,不会触发加载更多
     * @param str
     */
    private void showLoadingMore(CharSequence str) {
        mIsLoadingMore = true;
        mIsLoadingComplete = false;
        mIsLoadingError = false;
        mAdapter.showLoadingMore(str);
    }

    public void showLoadingError() {
        showLoadingError(mDefaultErrStr);
    }

    public void showLoadingError(int strResId) {
        showLoadingError(getResources().getString(strResId));
    }

    /**
     * 显示加载错误
     * @param str
     */
    public void showLoadingError(CharSequence str) {
        mIsLoadingMore = false;
        mIsLoadingComplete = false;
        mIsLoadingError = true;
        mAdapter.showLoadingError(str);
    }

    public void showLoadingComplete() {
        showLoadingComplete(mDefLoadingCompleteStr);
    }

    public void showLoadingComplete(int strResId) {
        showLoadingComplete(getResources().getString(strResId));
    }

    /**
     * 显示加载完成,调用该方法后,不会再触发加载更多
     * @param str
     */
    public void showLoadingComplete(CharSequence str) {
        mIsLoadingMore = false;
        mIsLoadingComplete = true;
        mIsLoadingError = false;
        mAdapter.showLoadingComplete(str);
    }

    /**
     * 系统在加载完某一页数据后,如果还有更多数据,则调用此方法,此时控件可以再次触发加载更多
     */
    public void loadMoreSuccess() {
        mIsLoadingMore = false;
        mIsLoadingComplete = false;
        mIsLoadingError = false;
    }

    /**
     * 重置到初始化状态
     */
    public void resetLoadingState() {
        showLoadingMore();
    }

    /**
     * 数据为空时,显示图片提示
     */
    public void showDataEmptyImage() {
        mIsLoadingMore = false;
        mIsLoadingComplete = true;
        mIsLoadingError = false;
        mAdapter.hideLoadingView();
        mAdapter.showDataEmptyImage();
    }

    /**
     * 无网络或加载错误是,显示错误提示图片
     */
    public void showDataErrorImage() {
        mIsLoadingMore = false;
        mIsLoadingComplete = true;
        mIsLoadingError = false;
        mAdapter.hideLoadingView();
        mAdapter.showDataErrorImage();
    }
}
