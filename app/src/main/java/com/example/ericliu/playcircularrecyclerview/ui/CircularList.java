package com.example.ericliu.playcircularrecyclerview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.ericliu.playcircularrecyclerview.R;

/**
 * TODO: document your custom view class.
 */
public class CircularList<T> extends FrameLayout {

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    // scroll the list to a pretty large position index so it can be scroll both up and down.
    private static final int A_BIG_NUMBER = 1000;

    // a flag to set the height of RecyclerView only once
    private boolean heightHasBeenSet = false;

    private RecyclerView mRecyclerView;
    private ListPresenter<T> mListPresenter;
    private MiddleItemScrollListener mScrollListener;

    /**
     * To use the CircularList, the client must call this method to supply a ListPresenter for
     * populating data.
     *
     * @param presenter
     */
    public void setPresenter(ListPresenter<T> presenter) {
        if (presenter == null) {
            return;
        }
        mListPresenter = presenter;
        mRecyclerView.setAdapter(new MiddleItemAdapter());
    }


    public CircularList(Context context) {
        super(context);
        init(null, 0);
    }

    public CircularList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircularList, defStyle, 0);

        a.recycle();
        initRecyclerView();
    }

    private void initRecyclerView() {
        // create an FrameLayout.LayoutParams to set the list to the center of the view
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mRecyclerView = new RecyclerView(getContext());
        this.addView(mRecyclerView, params);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mScrollListener = new MiddleItemScrollListener();
        mRecyclerView.setOnScrollListener(mScrollListener);
    }


    private static class MiddleItemScrollListener extends RecyclerView.OnScrollListener {
        private CustomViewHolder tempMiddleItemViewHolder;
        private int firstVisibleItem = 0;
        private int lastVisibleItem = 0;
        private int middleItem = 0;
        private LinearLayoutManager layoutManager;


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            highlightMiddleItem(recyclerView);
        }

        @Override
        public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
            layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // only show complete items, do not display partial items.
                layoutManager.scrollToPositionWithOffset(layoutManager.findFirstCompletelyVisibleItemPosition(), 0);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        highlightMiddleItem(recyclerView);
                    }
                });
            }
        }

        private void highlightMiddleItem(RecyclerView recyclerView) {
            firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
            lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
            middleItem = firstVisibleItem + (lastVisibleItem + 1 - firstVisibleItem) / 2;
            displayMiddleItem(recyclerView);
        }

        private void displayMiddleItem(RecyclerView recyclerView) {
            if (tempMiddleItemViewHolder != null) {
                tempMiddleItemViewHolder.setNormalRow();
            }
            tempMiddleItemViewHolder = (CustomViewHolder) recyclerView.findViewHolderForAdapterPosition(middleItem);
            if (tempMiddleItemViewHolder != null) {
                tempMiddleItemViewHolder.setMiddleRow();
            }
        }


    }

    private class MiddleItemAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CustomViewHolder holder;
            holder = mListPresenter.getCustomViewHolder(parent);
            return holder;
        }


        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            if (mListPresenter.getListLength() == 0) {
                // never divide a number by 0
                return;
            }
            int realPosition = position % mListPresenter.getListLength();
            holder.setItemData(mListPresenter.getItemAtPosition(realPosition));
            setRecyclerViewHeight(holder);
        }


        @Override
        public int getItemCount() {
            return MAX_VALUE;
        }


    }


    private void setRecyclerViewHeight(CustomViewHolder holder) {
        if (heightHasBeenSet) {
            return;
        }

        final View childView = holder.itemView;
        childView.post(new Runnable() {
            @Override
            public void run() {
                // set the height of the RecyclerView to just enough to display all items
                // if the CircularList's height is not enough to display all items, choose its height
                // as the RecyclerView's height
                int totalHeight = childView.getMeasuredHeight() * mListPresenter.getListLength();
                int parentHeight = CircularList.this.getMeasuredHeight();

                LayoutParams listParams = (LayoutParams) mRecyclerView.getLayoutParams();
                listParams.height = parentHeight > totalHeight ? totalHeight : parentHeight;
                listParams.gravity = Gravity.CENTER;

                mRecyclerView.setLayoutParams(listParams);

                if (mListPresenter.getListLength() != 0) {
                    mRecyclerView.scrollToPosition(mListPresenter.getListLength() * A_BIG_NUMBER);
                    mRecyclerView.invalidate();
                }
            }
        });
        
        // it's expensive operation, set the flag to prevent it from running again.
        heightHasBeenSet = true;
    }


    public static abstract class CustomViewHolder<T> extends RecyclerView.ViewHolder {
        protected T t;

        public CustomViewHolder(View itemView) {
            super(itemView);
        }

        public void setItemData(T t) {
            this.t = t;
            refreshView();
        }

        protected abstract void refreshView();

        protected abstract void setMiddleRow();

        public abstract void setNormalRow();
    }


    public interface ListPresenter<T> {
         // A common interface for all Presenters to implement

        CustomViewHolder getCustomViewHolder(ViewGroup parent);

        T getItemAtPosition(int position);

        int getListLength();

    }

}
