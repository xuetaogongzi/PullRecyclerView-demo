package com.wangjw.pullrecyclerviewlib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by wangjw on 16/11/2.
 */

public class WrappedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private RecyclerView.Adapter mAdapter;

    private View mLayoutLoading;
    private RelativeLayout mRlLoadingContainer;
    private TextView mTvLoading;
    private ProgressBar mPbLoading;

    private View mLayoutHint;
    private LinearLayout mLlHintContainer;
    private ImageView mImgHint;
    private Button mBtnRetry;

    private View.OnClickListener mOnErrorRetryClickListener;

    public WrappedRecyclerAdapter(Context context, RecyclerView.Adapter adapter) {
        mContext = context;
        mAdapter = adapter;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mLayoutLoading = inflater.inflate(R.layout.pr_layout_loading, null);
        mLayoutHint = inflater.inflate(R.layout.pr_layout_hint, null);

        mRlLoadingContainer = (RelativeLayout) mLayoutLoading.findViewById(R.id.RelativeLayout_Loading);
        mPbLoading = (ProgressBar) mLayoutLoading.findViewById(R.id.ProgressBar_Loading);
        mTvLoading = (TextView) mLayoutLoading.findViewById(R.id.TextView_Loading);

        mLlHintContainer = (LinearLayout) mLayoutHint.findViewById(R.id.LinearLayout_Hint);
        mImgHint = (ImageView) mLayoutHint.findViewById(R.id.ImageView_Hint);
        mBtnRetry = (Button) mLayoutHint.findViewById(R.id.Button_Hint_retry);

        mBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnErrorRetryClickListener != null) {
                    mOnErrorRetryClickListener.onClick(v);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return R.layout.pr_layout_hint;
        } else if (position == getItemCount() - 2) {
            return R.layout.pr_layout_loading;
        } else {
            return mAdapter.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == R.layout.pr_layout_hint) {
            RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(mLayoutHint) {

            };
            return holder;
        } else if (viewType == R.layout.pr_layout_loading) {
            RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(mLayoutLoading) {

            };
            return holder;
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount() - 2) {
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    public RecyclerView.Adapter getWrappedAdapter() {
        return mAdapter;
    }

    public void setOnErrorRetryClickListener(View.OnClickListener onClickListener) {
        mOnErrorRetryClickListener = onClickListener;
        mRlLoadingContainer.setOnClickListener(onClickListener);
    }

    /**
     * 显示加载更多
     * @param str
     */
    public void showLoadingMore(CharSequence str) {
        mRlLoadingContainer.setVisibility(View.VISIBLE);
        mPbLoading.setVisibility(View.VISIBLE);
        mTvLoading.setVisibility(View.VISIBLE);

        mTvLoading.setText(str);

        mLlHintContainer.setVisibility(View.GONE);
    }

    /**
     * 显示加载错误
     * @param str
     */
    public void showLoadingError(CharSequence str) {
        mRlLoadingContainer.setVisibility(View.VISIBLE);
        mPbLoading.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.VISIBLE);

        mTvLoading.setText(str);

        mLlHintContainer.setVisibility(View.GONE);
    }

    /**
     * 显示加载完成
     * @param str
     */
    public void showLoadingComplete(CharSequence str) {
        mRlLoadingContainer.setVisibility(View.VISIBLE);
        mPbLoading.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.VISIBLE);

        mTvLoading.setText(str);

        mLlHintContainer.setVisibility(View.GONE);
    }

    /**
     * 隐藏
     */
    public void hideLoadingView() {
        mRlLoadingContainer.setVisibility(View.GONE);
    }

    /**
     * 显示空数据
     */
    public void showDataEmptyImage() {
        mLlHintContainer.setVisibility(View.VISIBLE);
        mImgHint.setImageResource(R.drawable.ic_data_empty);
        mBtnRetry.setVisibility(View.GONE);
    }

    /**
     * 显示异常数据
     */
    public void showDataErrorImage() {
        mLlHintContainer.setVisibility(View.VISIBLE);
        mImgHint.setImageResource(R.drawable.ic_load_error);
        mBtnRetry.setVisibility(View.VISIBLE);
    }

}
