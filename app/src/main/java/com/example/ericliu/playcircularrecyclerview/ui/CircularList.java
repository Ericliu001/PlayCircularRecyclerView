package com.example.ericliu.playcircularrecyclerview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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


    /**
     * To use the CircularList, the client must call this method to supply a ListPresenter for
     * populating data.
     * @param presenter
     */
    public void setPresenter(ListPresenter<T> presenter) {
        if (presenter == null) {
            return;
        }
        mListPresenter = presenter;
        mRecyclerView.setAdapter(new MiddleItemAdapter());
        // call invalidate to change the middle item view before the user scrolls
//        mRecyclerView.invalidate();

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mListPresenter.getListLength() != 0) {
                    mRecyclerView.scrollToPosition(mListPresenter.getListLength() * A_BIG_NUMBER);
                    mRecyclerView.invalidate();
                }

            }
        });
    }

    private RecyclerView mRecyclerView;
    private ListPresenter<T> mListPresenter;
    private MiddleItemScrollListener mScrollListener;

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
        mRecyclerView = new RecyclerView(getContext());
        this.addView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mScrollListener = new MiddleItemScrollListener();
        mRecyclerView.setOnScrollListener(mScrollListener);
    }




    private static class MiddleItemScrollListener extends RecyclerView.OnScrollListener {
        private CustomViewHolder middleRowHolder;
        int firstVisibleItem = 0;
        int lastVisibleItem = 0;
        int middleItem = 0;


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            middleItem = (firstVisibleItem + lastVisibleItem) / 2;
            displayMiddleItem(recyclerView);
        }

        private void displayMiddleItem(RecyclerView recyclerView) {
            if (middleRowHolder != null) {
                middleRowHolder.setNormalRow();
            }
            middleRowHolder = (CustomViewHolder) recyclerView.findViewHolderForAdapterPosition(middleItem);
            if (middleRowHolder != null) {
                middleRowHolder.setMiddleRow();
            }
        }


        public int getMiddleItemPosition() {
            return middleItem;
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


        }


        @Override
        public int getItemCount() {
            return MAX_VALUE;
        }


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

        /**
         * A common interface for all Presenters to implement
         */

        CustomViewHolder getCustomViewHolder(ViewGroup parent);


        T getItemAtPosition(int position);

        int getListLength();

    }

}
