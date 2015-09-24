package com.example.ericliu.playcircularrecyclerview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.ericliu.playcircularrecyclerview.R;

/**
 * TODO: document your custom view class.
 */
public class CircularList extends FrameLayout {


    private RecyclerView mRecyclerView;
    private ListPresenter mListPresenter;
    private MiddleItemScrollListener scrollListener;

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
        scrollListener = new MiddleItemScrollListener();
        mRecyclerView.setOnScrollListener(scrollListener);
    }

    public void setPresenter(ListPresenter presenter) {
        if (presenter == null) {
            return;
        }
        mListPresenter = presenter;
        mRecyclerView.setAdapter(new MiddleItemAdapter());
    }





    private static class MiddleItemScrollListener extends RecyclerView.OnScrollListener {

        int firstVisibleItem = 0;
        int lastVisibleItem = 0;
        int middleItem = 0;



        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            middleItem = (firstVisibleItem + lastVisibleItem)/2;
        }


        public int getMiddleItemPosition() {
            return middleItem;
        }
    }

    private class MiddleItemAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private static final int ITEM_VIEW_TYPE_MIDDLE_ITEM = 1;
        private static final int ITEM_VIEW_TYPE_NORMAL_ITEM = 2;

        @Override
        public int getItemViewType(int position) {
            return position == scrollListener.getMiddleItemPosition()
                    ? ITEM_VIEW_TYPE_MIDDLE_ITEM
                    : ITEM_VIEW_TYPE_NORMAL_ITEM;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            CustomViewHolder holder;
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.circular_list_normal_row, parent, false);
            holder = mListPresenter.getCustomViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            int realPosition = position % mListPresenter.getListLength();
            holder.setItemData(mListPresenter.getItemAtPosition(realPosition));
        }



        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }


    }
        public static abstract class CustomViewHolder<T> extends RecyclerView.ViewHolder  {
            protected T t;



            public CustomViewHolder(View itemView) {
                super(itemView);

            }


            public void setItemData(T t) {
                this.t = t;
                refreshView();
            }

            protected abstract void refreshView();
        }


    public interface ListPresenter {

        /**
         * A common interface for all Presenters to implement
         */
        void onPostViewCreated();


        CustomViewHolder getCustomViewHolder(View itemView);

        Object getItemAtPosition(int position);

        int getListLength();

    }

}
