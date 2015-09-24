package com.example.ericliu.playcircularrecyclerview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.ericliu.playcircularrecyclerview.R;

/**
 * TODO: document your custom view class.
 */
public class CircularList extends FrameLayout {


    private RecyclerView mRecyclerView;

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



        mRecyclerView = new RecyclerView(getContext());
        this.addView(mRecyclerView);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setOnScrollListener(new MiddleItemScrollListener());
    }





    public static class MiddleItemScrollListener extends RecyclerView.OnScrollListener {

        int firstVisibleItem = 0;
        int lasstVisibleItem = 0;
        int middleItem = 0;



        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            lasstVisibleItem = layoutManager.findLastVisibleItemPosition();
            middleItem = (firstVisibleItem + lasstVisibleItem)/2;
        }


    }
}
